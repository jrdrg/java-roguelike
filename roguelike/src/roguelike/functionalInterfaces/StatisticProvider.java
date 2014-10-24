package roguelike.functionalInterfaces;

import java.io.Serializable;

import roguelike.actors.Statistics;
import roguelike.actors.Statistics.Statistic;

public interface StatisticProvider extends Serializable {
	public Statistic get(Statistics statistics);
}
