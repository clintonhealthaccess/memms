package org.chai.memms.task

interface Progress {
	public void incrementProgress();
	
	public void incrementProgress(Long increment);
	
	public Double retrievePercentage();
	
	public void setMaximum(Long max);
	
	public void abort();
	
	public boolean isAborted();
}
