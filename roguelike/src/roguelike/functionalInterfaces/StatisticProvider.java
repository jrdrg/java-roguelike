package roguelike.functionalInterfaces;

import roguelike.actors.Statistics;
import roguelike.actors.Statistics.Statistic;

public interface StatisticProvider {
	public Statistic get(Statistics statistics);
}
