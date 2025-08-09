package problems.bbob.utils;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import problems.bbob.BBOBProblem;
import problems.bbob.f2_ellipsoid_sep.EllipsoidSeparable;

public class SurfacePlot3D extends Application {

    private static BBOBProblem problemToPlot;

    public static void main(String[] args) {
        show(new EllipsoidSeparable(2));
    }

    public static void show(BBOBProblem problem) {
        problemToPlot = problem;
        launch();
    }

    @Override
    public void start(Stage stage) {
        if (problemToPlot.getDimension() != 2) {
            throw new IllegalArgumentException("Only 2D problems are supported.");
        }

        Group root = new Group();
        MeshView surface = createColoredSurface(problemToPlot);
        root.getChildren().add(surface);

        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-500);
        light.setTranslateY(-300);
        light.setTranslateZ(-700);

        AmbientLight ambient = new AmbientLight(Color.color(0.3, 0.3, 0.3));

        root.getChildren().addAll(light, ambient);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-600);
        camera.setTranslateX(-150);
        camera.setTranslateY(-150);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);

        Rotate xRotate = new Rotate(-45, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(-45, Rotate.Y_AXIS);
        surface.getTransforms().addAll(xRotate, yRotate);

        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);
        scene.setCamera(camera);

        stage.setTitle("3D Surface Plot with Height Coloring");
        stage.setScene(scene);
        stage.show();
    }

    private MeshView createColoredSurface(BBOBProblem problem) {
        float xMin = (float) problem.lowerBound(0);
        float xMax = (float) problem.upperBound(0);
        float yMin = (float) problem.lowerBound(1);
        float yMax = (float) problem.upperBound(1);

        int steps = 100;
        float[][] zValues = new float[steps + 1][steps + 1];
        float zMin = Float.MAX_VALUE, zMax = -Float.MAX_VALUE;

        float xStep = (xMax - xMin) / steps;
        float yStep = (yMax - yMin) / steps;

        for (int i = 0; i <= steps; i++) {
            for (int j = 0; j <= steps; j++) {
                float x = xMin + i * xStep;
                float y = yMin + j * yStep;
                float z = (float) problem.evaluate(new double[]{x, y});
                zValues[i][j] = z;
                zMin = Math.min(zMin, z);
                zMax = Math.max(zMax, z);
            }
        }

        TriangleMesh mesh = new TriangleMesh();

        // Add points
        for (int i = 0; i <= steps; i++) {
            for (int j = 0; j <= steps; j++) {
                float x = xMin + i * xStep;
                float y = yMin + j * yStep;
                float z = zValues[i][j];
                mesh.getPoints().addAll(x * 20, -z * 10, y * 20);
            }
        }

        // Generate texture coordinates (1D gradient, mapped by z value)
        int texSteps = 256;
        for (int i = 0; i < texSteps; i++) {
            float t = (float) i / (texSteps - 1);
            mesh.getTexCoords().addAll(t, 0);
        }

        // Add faces
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                int p0 = i * (steps + 1) + j;
                int p1 = p0 + 1;
                int p2 = p0 + (steps + 1);
                int p3 = p2 + 1;

                float zA = zValues[i][j];
                float zB = zValues[i][j + 1];
                float zC = zValues[i + 1][j];
                float zD = zValues[i + 1][j + 1];

                int ta = zToTexIndex(zA, zMin, zMax, texSteps);
                int tb = zToTexIndex(zB, zMin, zMax, texSteps);
                int tc = zToTexIndex(zC, zMin, zMax, texSteps);
                int td = zToTexIndex(zD, zMin, zMax, texSteps);

                // Two triangles per quad
                mesh.getFaces().addAll(p0, ta, p2, tc, p1, tb);
                mesh.getFaces().addAll(p1, tb, p2, tc, p3, td);
            }
        }

        MeshView meshView = new MeshView(mesh);
        meshView.setDrawMode(DrawMode.FILL);
        meshView.setCullFace(CullFace.NONE);

        // Generate gradient image and apply as texture
        Image gradient = createGradientImage(texSteps);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(gradient);
        meshView.setMaterial(material);

        return meshView;
    }

    private int zToTexIndex(float z, float zMin, float zMax, int texSteps) {
        float normalized = (z - zMin) / (zMax - zMin);
        return Math.max(0, Math.min(texSteps - 1, (int) (normalized * (texSteps - 1))));
    }

    private Image createGradientImage(int width) {
        WritableImage image = new WritableImage(width, 1);
        PixelWriter writer = image.getPixelWriter();
        for (int i = 0; i < width; i++) {
            double t = (double) i / (width - 1);
            writer.setColor(i, 0, heightColorMap(t));
        }
        return image;
    }

    private Color heightColorMap(double t) {
        // Blue → Green → Yellow → Red gradient
        if (t < 0.33) return Color.BLUE.interpolate(Color.GREEN, t / 0.33);
        else if (t < 0.66) return Color.GREEN.interpolate(Color.YELLOW, (t - 0.33) / 0.33);
        else return Color.YELLOW.interpolate(Color.RED, (t - 0.66) / 0.34);
    }
}
