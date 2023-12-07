package experiments.motap;

import metaheuristic.MetaHeuristicService;
import metaheuristic.ea.EAService;
import metaheuristic.tabu.TabuService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//Comparison of 6 Algorithms (GA, MBO, PMBO, IslanMBO, Island-GA) on the normal  instances (Task:20,30,40 Processor:8)  with fixed neighboring function count.
public class MOTAPComparison1TestCases {


	    private static final int repeat = 1;  // Repetition count

	    private static final String[] terminalConditions = {EAService.TerminalConditions.MotapNeighboringBased+addParams(1000)};
	    
	    /*PARAMETER TUNING*/
	    public static List<ITestCaseGenerator> MetaHeuristics = Arrays.asList(/*new EACases(), new MBOCases(),*/ new PMBOCases()/*\ new IslandEACases(),*//* new IslandMBOCases()/*,  new IslandSSOCases()/*,new SSOCases() */);
	    
	    
	    public interface ITestCaseGenerator{
	        public  void generateCases(BufferedWriter writer, File[] files) throws IOException;
	    }


	    public static class EACases implements ITestCaseGenerator{

	        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

	        private static final String[] mutationOperators = {EAService.MutationOperators.GreedySwapWithExchange+addParams(3)};
	        private static final String[] mutationStrategies = {EAService.MutationStrategies.Simple_02_1};

	        private static final String[] crossOverOperators = {EAService.CrossOverOperators.SimpleMOTAP};
	        private static final String[] crossOverStrategies = {EAService.CrossOverStrategies.Simple_08};

	        private static final String[] victimSelectors = {EAService.VictimSelectors.Simple};
	        private static final String[] parentSelectors = { EAService.ParentSelectors.RouletteWheel};

	        private static final int[] initialPopulationSizes = {40, 60, 80};

	        public void generateCases(BufferedWriter writer,File[] files) throws IOException {

	            for (int isg =0; isg<solutionGenerators.length;isg++) {
	                for (int mo = 0; mo < mutationOperators.length; mo++) {
	                    for (int ms = 0; ms < mutationStrategies.length; ms++) {
	                        for (int co = 0; co < crossOverOperators.length; co++) {
	                            for (int cs = 0; cs < crossOverStrategies.length; cs++) {
	                                for (int vs = 0; vs < victimSelectors.length; vs++) {
	                                    for (int ps = 0; ps < parentSelectors.length; ps++) {
	                                        for (int tc = 0; tc < terminalConditions.length; tc++) {
	                                            for (int ips = 0; ips < initialPopulationSizes.length; ips++) {
	                                                    for (File file:files) {
	                                                        String prefix = file.getName();
	                                                        if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
                                                                continue;
                                                            if(prefix.indexOf("20_") >= 0 && initialPopulationSizes[ips] != 40)
                                                            	continue;
                                                            if(prefix.indexOf("30_") >= 0 && initialPopulationSizes[ips] != 60)
                                                            	continue;
                                                            if(prefix.indexOf("40_") >= 0 && initialPopulationSizes[ips] != 80)
                                                            	continue;
	                                                        writeRepeated(writer, repeat, prefix + "\t" +
	                                                                MetaHeuristicService.M_EA + "\t" +
	                                                                solutionGenerators[isg] + "\t" +
	                                                                mutationOperators[mo] + "\t" +
	                                                                mutationStrategies[ms] + "\t" +
	                                                                crossOverOperators[co] + "\t" +
	                                                                crossOverStrategies[cs] + "\t" +
	                                                                victimSelectors[vs] + "\t" +
	                                                                parentSelectors[ps] + "\t" +
	                                                                terminalConditions[tc] + "\t" +
	                                                                initialPopulationSizes[ips] + "\n");

	                                                    }
	                                                }
	                                        }

	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }
	    
	    public static class MBOCases implements ITestCaseGenerator{
	        
	    	public static String MBO_NF[] = {EAService.MutationOperators.GreedySwapWithExchange+addParams(3)};

	        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

	        public static int nPopulations[] = {51};   // "13", "25", "51"
	        public static int nTours[] = {10};    // "5", "10", "20"
	        public static int nNeighborSolutions[] = {3};   // 3, 5, 7
	        public static int nSharedNSolutions[] = {1};   // 1, 2, 3
	        public static String nRNG[] = {EAService.RNGs.MersenneTwister};//{EAService.RNGs.MersenneTwister, EAService.RNGs.Arnold, EAService.RNGs.Burgers, EAService.RNGs.Lozi, EAService.RNGs.Tent};

	        public void generateCases(BufferedWriter writer, File[]  files ) throws IOException {

	            for (int isg = 0; isg < solutionGenerators.length; isg++) {
	                for (int l = 0; l < MBO_NF.length; l++) {
	                    for (int tc = 0; tc < terminalConditions.length; tc++) {
	                        for (int p = 0; p < nPopulations.length; p++) {
	                            for (int t = 0; t < nTours.length; t++) {
	                                for (int ns = 0; ns < nNeighborSolutions.length; ns++) {
	                                    for (int ss = 0; ss < nSharedNSolutions.length; ss++) {
	                                        for (int r = 0; r < nRNG.length; r++) {
	                                            for (File file:files) {
	                                                String prefix = file.getName();

	                                                if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
	                                                    continue;
	                                                writeRepeated(writer, repeat, prefix + "\t" +
	                                                        MetaHeuristicService.M_MBO + "\t" +
	                                                        solutionGenerators[isg] + "\t" +
	                                                        MBO_NF[l] + "\t" +
	                                                        terminalConditions[tc] + "\t" +
	                                                        nPopulations[p] + "\t" +
	                                                        nTours[t] + "\t" +
	                                                        nNeighborSolutions[ns] + "\t" +
	                                                        nSharedNSolutions[ss] + "\t" +
	                                                        nRNG[r] + "\n");
	                                            }
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }

	    }
	    
	    public static class PMBOCases implements ITestCaseGenerator{
	        public static String MBO_NF[] = {EAService.MutationOperators.GreedySwapWithExchange+addParams(10)};

	        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

	        public static int nPopulations[] = {51};   // "13", "25", "51"
	        public static int nTours[] = {10};    // "5", "10", "20"
	        public static int nNeighborSolutions[] = {3};   // 3, 5, 7
	        public static int nSharedNSolutions[] = {1};   // 1, 2, 3
	        public static String nRNG[] = {EAService.RNGs.MersenneTwister};//{EAService.RNGs.MersenneTwister, EAService.RNGs.Arnold, EAService.RNGs.Burgers, EAService.RNGs.Lozi, EAService.RNGs.Tent};

	        public static int poolSizes[] = {16/*, 2, 4, 8, 16*/};

	        public void generateCases(BufferedWriter writer, File[]  files ) throws IOException {

	            for (int isg = 0; isg < solutionGenerators.length; isg++) {
	                for (int l = 0; l < MBO_NF.length; l++) {
	                    for (int tc = 0; tc < terminalConditions.length; tc++) {
	                        for (int p = 0; p < nPopulations.length; p++) {
	                            for (int t = 0; t < nTours.length; t++) {
	                                for (int ns = 0; ns < nNeighborSolutions.length; ns++) {
	                                    for (int ss = 0; ss < nSharedNSolutions.length; ss++) {
	                                        for (int r = 0; r < nRNG.length; r++) {
	                                            for (int ps = 0; ps < poolSizes.length; ps++) {
	                                                for (File file:files) {
	                                                    String prefix = file.getName();

	                                                    if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
	                                                        continue;
	                                                    writeRepeated(writer, repeat, prefix + "\t" +
	                                                            MetaHeuristicService.M_PMBO + "\t" +
	                                                            solutionGenerators[isg] + "\t" +
	                                                            MBO_NF[l] + "\t" +
	                                                            terminalConditions[tc] + "\t" +
	                                                            nPopulations[p] + "\t" +
	                                                            nTours[t] + "\t" +
	                                                            nNeighborSolutions[ns] + "\t" +
	                                                            nSharedNSolutions[ss] + "\t" +
	                                                            nRNG[r] + "\t" +
	                                                            poolSizes[ps] + "\n");
	                                                }
	                                            }

	                                        }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }

	    }
	    
	    public static class IslandEACases implements ITestCaseGenerator{

	        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

	        private static final String[] mutationOperators = {EAService.MutationOperators.GreedySwapWithExchange+addParams(3)};
	        private static final String[] mutationStrategies = {EAService.MutationStrategies.Simple_02_1};

	        private static final String[] crossOverOperators = {EAService.CrossOverOperators.SimpleMOTAP};
	        private static final String[] crossOverStrategies = {EAService.CrossOverStrategies.Simple_08};

	        private static final String[] victimSelectors = {EAService.VictimSelectors.Simple};
	        private static final String[] parentSelectors = { EAService.ParentSelectors.RouletteWheel};


			private static final int[] initialPopulationSizes = {40, 60, 80};

			private static final int[] populationCounts = {16};

			private static final int[] immigrationPeriods = {20};

			private static final int[] immigrantCounts  =   {5};

			private static final int[] poolSizes  =   {16/*, 2, 4, 8, 16*/};


	        public void generateCases(BufferedWriter writer,File[] files) throws IOException {

	            for (int isg =0; isg<solutionGenerators.length;isg++) {
	                for (int mo = 0; mo < mutationOperators.length; mo++) {
	                    for (int ms = 0; ms < mutationStrategies.length; ms++) {
	                        for (int co = 0; co < crossOverOperators.length; co++) {
	                            for (int cs = 0; cs < crossOverStrategies.length; cs++) {
	                                for (int vs = 0; vs < victimSelectors.length; vs++) {
	                                    for (int ps = 0; ps < parentSelectors.length; ps++) {
	                                        for (int tc = 0; tc < terminalConditions.length; tc++) {
	                                            for (int ips = 0; ips < initialPopulationSizes.length; ips++) {
	                                                for (int pc = 0; pc < populationCounts.length; pc++) {
	                                                    for (int ip = 0; ip < immigrationPeriods.length; ip++) {
	                                                        for (int ic = 0; ic < immigrantCounts.length; ic++) {
	                                                            for (int pools = 0; pools < poolSizes.length; pools++) {
	                                                                for (File file:files) {
	                                                                    String prefix = file.getName();

	                                                                    if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
	                                                                        continue;
	                                                                    if(prefix.indexOf("20_") >= 0 && initialPopulationSizes[ips] != 40)
	                                                                    	continue;
	                                                                    if(prefix.indexOf("30_") >= 0 && initialPopulationSizes[ips] != 60)
	                                                                    	continue;
	                                                                    if(prefix.indexOf("40_") >= 0 && initialPopulationSizes[ips] != 80)
	                                                                    	continue;
	                                                                    writeRepeated(writer, repeat, prefix + "\t" +
	                                                                            MetaHeuristicService.M_IEA + "\t" +
	                                                                            solutionGenerators[isg] + "\t" +
	                                                                            mutationOperators[mo] + "\t" +
	                                                                            mutationStrategies[ms] + "\t" +
	                                                                            crossOverOperators[co] + "\t" +
	                                                                            crossOverStrategies[cs] + "\t" +
	                                                                            victimSelectors[vs] + "\t" +
	                                                                            parentSelectors[ps] + "\t" +
	                                                                            terminalConditions[tc] + "\t" +
	                                                                            initialPopulationSizes[ips] + "\t"+
	                                                                            populationCounts[pc] + "\t" +
	                                                                            immigrationPeriods[ip] + "\t" +
	                                                                            immigrantCounts[ic] + "\t" +
	                                                                            poolSizes[pools] +"\n");

	                                                                }
	                                                            }
	                                                        }
	                                                    }
	                                                }

	                                            }
	                                        }

	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }


	    }


	public static class IslandMBOCases implements ITestCaseGenerator {

		public static String MBO_NF[] = {EAService.MutationOperators.GreedySwapWithExchange+addParams(3)};

		private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};


		public static int nPopulations[] = {51};   // "13", "25", "51"
		public static int nTours[] = {10};    // "5", "10", "20"
		public static int nNeighborSolutions[] = {3};   // 3, 5, 7
		public static int nSharedNSolutions[] = {1};   // 1, 2, 3
		public static String nRNG[] = {EAService.RNGs.MersenneTwister};//{EAService.RNGs.MersenneTwister, EAService.RNGs.Arnold, EAService.RNGs.Burgers, EAService.RNGs.Lozi, EAService.RNGs.Tent};




		//private static final int[] populationCounts = {2, 4, 8, 16};
		private static final int[] populationCounts = {16};

		//private static final int[] immigrationPeriods = {5, 10, 20, 50};
		private static final int[] immigrationPeriods = {50};

		//private static final int[] immigrantCounts  =   {2, 3, 4, 5};
		private static final int[] immigrantCounts  =   {4};

		private static final int[] poolSizes  =   {/*1, 2, 4, 8,*/ 16};

		public void generateCases(BufferedWriter writer, File[]  files ) throws IOException {

			for (int isg = 0; isg < solutionGenerators.length; isg++) {
				for (int l = 0; l < MBO_NF.length; l++) {
					for (int tc = 0; tc < terminalConditions.length; tc++) {
						for (int p = 0; p < nPopulations.length; p++) {
							for (int t = 0; t < nTours.length; t++) {
								for (int ns = 0; ns < nNeighborSolutions.length; ns++) {
									for (int ss = 0; ss < nSharedNSolutions.length; ss++) {
										for (int r = 0; r < nRNG.length; r++) {
											for (int pc = 0; pc < populationCounts.length; pc++) {
												for (int ip = 0; ip < immigrationPeriods.length; ip++) {
													for (int ic = 0; ic < immigrantCounts.length; ic++) {
														for (int pools = 0; pools < poolSizes.length; pools++) {
															for (File file : files) {
																String prefix = file.getName();

																if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
																	continue;
																writeRepeated(writer, repeat, prefix + "\t" +
																		MetaHeuristicService.M_IMBO + "\t" +
																		solutionGenerators[isg] + "\t" +
																		terminalConditions[tc] + "\t" +
																		populationCounts[pc] +  "\t" +
																		immigrationPeriods[ip] + "\t" +
																		immigrantCounts[ic] +  "\t" +
																		poolSizes[pools] +"\t"+
																		MBO_NF[l] + "\t" +
																		terminalConditions[tc] + "\t" +
																		nPopulations[p] + "\t" +
																		nTours[t] + "\t" +
																		nNeighborSolutions[ns] + "\t" +
																		nSharedNSolutions[ss] + "\t" +
																		nRNG[r] + "\n" );
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}



	public static class HBMOCases implements ITestCaseGenerator {
		private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

		public static String MBO_NF[] = {EAService.MutationOperators.GreedySearch,
				//makeList(EAService.MutationOperators.GreedySwap+addParams(5),EAService.MutationOperators.SimpleMotap)
		};

		private static final String[] crossOverOperators = {EAService.CrossOverOperators.SimpleMOTAP};

		public static int drs[] = {35};   // "13", "25", "51"
		public static int spmaxs[] = {10};    // "5", "10", "20"
		public static int bmaxs[] = {3};   // 3, 5, 7
		public static double darsads[] = {0.1};   // 1, 2, 3

		public void generateCases(BufferedWriter writer, File[] files) throws IOException {

			for (int isg = 0; isg < solutionGenerators.length; isg++) {
				for (int l = 0; l < MBO_NF.length; l++) {
					for (int tc = 0; tc < terminalConditions.length; tc++) {
						for (int co = 0; co < crossOverOperators.length; co++) {
							for (int d = 0; d < drs.length; d++) {
								for (int s = 0; s < spmaxs.length; s++) {
									for (int b = 0; b < bmaxs.length; b++) {
										for (int ds = 0; ds < darsads.length; ds++) {
											for (File file : files) {
												String prefix = file.getName();

												if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
													continue;
												writeRepeated(writer, repeat, prefix + "\t" +
														MetaHeuristicService.M_HBMO + "\t" +
														solutionGenerators[isg] + "\t" +
														MBO_NF[l] + "\t" +
														terminalConditions[tc] + "\t" +
														crossOverOperators[co] + "\t" +
														drs[d] + "\t" +
														spmaxs[s] + "\t" +
														bmaxs[b] + "\t" +
														darsads[ds] + "\n");
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

		public static class IslanHBMOCases implements ITestCaseGenerator {
			private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};


			public static int moduleCounts[] = {16};   // "13", "25", "51"
			public static int migrationPeriods[] = {10};    // "5", "10", "20"
			public static int immigrantCounts[] = {4};
			public static int poolSizes[] = {1,2,4,8,16};

			public static String MBO_NF[] = {   EAService.MutationOperators.GreedySearch ,
					//makeList(EAService.MutationOperators.GreedySwap+addParams(5),EAService.MutationOperators.SimpleMotap)
			};

			private static final String[] crossOverOperators = {EAService.CrossOverOperators.SimpleMOTAP};

			public static int drs[] = {35};   // "13", "25", "51"
			public static int spmaxs[] = {10};    // "5", "10", "20"
			public static int bmaxs[] = {3};   // 3, 5, 7
			public static double darsads[] = {0.1};   // 1, 2, 3

			public void generateCases(BufferedWriter writer, File[]  files ) throws IOException {

				for (int isg = 0; isg < solutionGenerators.length; isg++) {
					for (int tc = 0; tc < terminalConditions.length; tc++) {
					for (int mc = 0; mc < moduleCounts.length; mc++) {
						for (int mp = 0; mp < migrationPeriods.length; mp++) {
							for (int ic = 0; ic < immigrantCounts.length; ic++) {
								for (int ps = 0; ps < poolSizes.length; ps++) {
								for (int l = 0; l < MBO_NF.length; l++) {
									for (int co = 0; co < crossOverOperators.length; co++) {
										for (int d = 0; d < drs.length; d++) {
											for (int s = 0; s < spmaxs.length; s++) {
												for (int b = 0; b < bmaxs.length; b++) {
													for (int ds = 0; ds < darsads.length; ds++) {
														for (File file : files) {
															String prefix = file.getName();

															if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
																continue;
															writeRepeated(writer, repeat, prefix + "\t" +
																	MetaHeuristicService.M_IHBMO + "\t" +
																	solutionGenerators[isg] + "\t" +
																	terminalConditions[tc] + "\t" +
																	moduleCounts[mc] + "\t" +
																	migrationPeriods[mp] + "\t" +
																	immigrantCounts[ic] + "\t" +
																	poolSizes[ps] + "\t" +
																	MBO_NF[l] + "\t" +
																	terminalConditions[tc] + "\t" +
																	crossOverOperators[co] + "\t" +
																	drs[d] + "\t" +
																	spmaxs[s] + "\t" +
																	bmaxs[b] + "\t" +
																	darsads[ds] + "\n");
														}
													}
												}
											}
										}
									}
								}
								}
							}
						}
					}
					}}}


		}

	public static class SSOCases implements ITestCaseGenerator {

		private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

		public static int particleCounts[] = {50};

		public void generateCases(BufferedWriter writer, File[] files ) throws IOException {

			for (int isg = 0; isg < solutionGenerators.length; isg++) {
				for (int tc = 0; tc < terminalConditions.length; tc++) {
					for (int pc = 0; pc < particleCounts.length; pc++) {
						for (File file:files) {
							String prefix = file.getName();

							if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
								continue;
							writeRepeated(writer, repeat, prefix + "\t" +
									MetaHeuristicService.M_SSO + "\t" +
									solutionGenerators[isg] + "\t" +
									terminalConditions[tc] + "\t"+
									particleCounts[pc]+"\n" );
						}
					}
				}

			}

		}

	}

	public static class IslandSSOCases implements ITestCaseGenerator {
		private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};


		public static int moduleCounts[] = {16};   // "13", "25", "51"
		public static int migrationPeriods[] = {100};    // "5", "10", "20"
		public static int immigrantCounts[] = {4};
		public static int poolSizes[] = {16/*,2,4,8,16*/};


		public static int particleCounts[] = {20};


		public void generateCases(BufferedWriter writer, File[]  files ) throws IOException {

			for (int isg = 0; isg < solutionGenerators.length; isg++) {
				for (int tc = 0; tc < terminalConditions.length; tc++) {
					for (int mc = 0; mc < moduleCounts.length; mc++) {
						for (int mp = 0; mp < migrationPeriods.length; mp++) {
							for (int ic = 0; ic < immigrantCounts.length; ic++) {
								for (int ps = 0; ps < poolSizes.length; ps++) {
									for (int pc = 0; pc < particleCounts.length; pc++) {
										for (File file : files) {
											String prefix = file.getName();

											if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
												continue;
											writeRepeated(writer, repeat, prefix + "\t" +
													MetaHeuristicService.M_ISSO + "\t" +
													solutionGenerators[isg] + "\t" +
													terminalConditions[tc] + "\t" +
													moduleCounts[mc] + "\t" +
													migrationPeriods[mp] + "\t" +
													immigrantCounts[ic] + "\t" +
													poolSizes[ps] + "\t" +
													terminalConditions[tc] + "\t" +
													particleCounts[pc] + "\n");
										}
									}
								}

							}
						}
					}
				}
			}
		}

	}



	public static String makeList(String...items)
	    {
	        String s = "";
	        for (String param:items)
	            s += param + EAService.OBJECT_DELIMITER;

	        return s;
	    }

	    public static String addParams(Object ...params )
	    {
	        String s = "";
	        for (Object param:params)
	            s += TabuService.PARAMS_DELIMITER + param;

	        return s;
	    }

	    private static void writeRepeated(BufferedWriter writer, int run, String s) throws IOException {
	        for (int i =0;i<run;i++ )
	        {
	            writer.write(s);
	        }
	    }


	    public static void generateTestCases(BufferedWriter writer, File[] files) throws IOException {
	        for (ITestCaseGenerator generator:MetaHeuristics)
	        {
	            generator.generateCases(writer,files);
	        }
	    }

	}


