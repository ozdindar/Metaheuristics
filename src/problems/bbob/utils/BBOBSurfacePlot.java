package problems.bbob.utils;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import problems.bbob.BBOBProblem;
import problems.bbob.f1_Sphere.Sphere;
import problems.bbob.f8_rosenbrock.Rosenbrock;

public class BBOBSurfacePlot extends Application {

    static BBOBProblem problem = new Rosenbrock(2);

    private static final int RESOLUTION = 100;
    private static final float SCALE = 50f;



    public void start(Stage stage) {
        if (problem.getDimension() != 2) {
            throw new IllegalArgumentException("Only 2D problems are supported.");
        }

        Group root = new Group();
        MeshView surface = createSurfaceMesh(problem);
        root.getChildren().add(surface);

        AmbientLight light = new AmbientLight(Color.WHITE);
        root.getChildren().add(light);



        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-2000);
        camera.setTranslateX(-100);
        camera.setTranslateY(-100);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setFieldOfView(30);

        Rotate xRotate = new Rotate(-45, Rotate.X_AXIS);
        Rotate yRotate = new Rotate(-45, Rotate.Y_AXIS);
        surface.getTransforms().addAll(xRotate, yRotate);

        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);
        scene.setCamera(camera);

        stage.setTitle("3D Surface Plot");
        stage.setScene(scene);
        stage.show();
    }

/*
    @Override
    public void start(Stage primaryStage) {
        TriangleMesh mesh = createTestMesh();

        MeshView meshView = new MeshView(mesh);
        meshView.setMaterial(new PhongMaterial(Color.CORNFLOWERBLUE));
        meshView.setDrawMode(DrawMode.FILL);
        meshView.setCullFace(CullFace.NONE); // Optional: show both sides

        meshView.setTranslateX(400);
        meshView.setTranslateY(300);
        meshView.setTranslateZ(0);

        Group root = new Group(meshView);

        // Add lighting
        AmbientLight light = new AmbientLight(Color.WHITE);
        root.getChildren().add(light);

        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-1000);
        scene.setCamera(camera);

        primaryStage.setTitle("BBOB Function Surface Plot");
        primaryStage.setScene(scene);
        primaryStage.show();
    }*/


    private MeshView createSurfaceMesh(BBOBProblem problem) {
        TriangleMesh mesh = new TriangleMesh();

        float xMin = (float) problem.lowerBound(0);
        float xMax = (float) problem.upperBound(0);
        float yMin = (float) problem.lowerBound(1);
        float yMax = (float) problem.upperBound(1);

        float dx = (xMax - xMin) / (RESOLUTION - 1);
        float dy = (yMax - yMin) / (RESOLUTION - 1);

        // Points
        for (int yi = 0; yi < RESOLUTION; yi++) {
            for (int xi = 0; xi < RESOLUTION; xi++) {
                float x = xMin + xi * dx;
                float y = yMin + yi * dy;
                double z = problem.evaluate(new double[]{x, y});
                mesh.getPoints().addAll(x * SCALE, y * SCALE, (float) z);
            }
        }

        // Texture coordinates (dummy)
        mesh.getTexCoords().addAll(0, 0);

        // Faces
        for (int yi = 0; yi < RESOLUTION - 1; yi++) {
            for (int xi = 0; xi < RESOLUTION - 1; xi++) {
                int p0 = yi * RESOLUTION + xi;
                int p1 = p0 + 1;
                int p2 = p0 + RESOLUTION;
                int p3 = p2 + 1;

                mesh.getFaces().addAll(p0, 0, p2, 0, p1, 0);
                mesh.getFaces().addAll(p1, 0, p2, 0, p3, 0);
            }
        }

        MeshView meshView = new MeshView(mesh);
        meshView.setDrawMode(DrawMode.FILL);
        meshView.setCullFace(CullFace.NONE);
        meshView.setMaterial(new PhongMaterial(Color.DARKCYAN));

        return meshView;
    }

    public static void generate(BBOBProblem problem)
    {
        BBOBSurfacePlot.problem = problem;
        Application.launch(BBOBSurfacePlot.class);
    }

    public static void main(String[] args) {
        generate(new Sphere(2));
    }
}
