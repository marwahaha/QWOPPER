package com.slowfrog.qwop;

public class MinRatioFilter implements IFilter<Individual> {

  private final float minRatio;

  private final IFilter<RunInfo> filter;
  /**
   * defaults to minRatio of 0.5
   * @param filter
   */
  public MinRatioFilter(IFilter<RunInfo> filter) {
    this(filter, 0.5f);
  }
  
  public MinRatioFilter(IFilter<RunInfo> filter, float minRatio) {
    this.filter = filter;
    this.minRatio = minRatio;
  }

  @Override
  public boolean matches(Individual individual) {
    if ((individual.runs.isEmpty()) && (minRatio > 0)) {
      return false;
    }
    int matchingRuns = 0;
    for (RunInfo run : individual.runs) {
      if (filter.matches(run)) {
        ++matchingRuns;
      }
    }
    return (((float) matchingRuns) / individual.runs.size()) >= this.minRatio;
  }

}
