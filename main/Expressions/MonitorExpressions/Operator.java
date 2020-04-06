package Expressions.MonitorExpressions;

import BuildingBlocks.STLFormulaMapper;
import BuildingBlocks.TrajectoryRecord;
import Expressions.ValueExpressions.Perc;
import it.units.malelab.jgea.core.Node;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.formula.DoubleDomain;
import eu.quanticol.moonlight.formula.Interval;

import java.util.List;


public enum Operator implements MonitorExpression {

    NOT(".not"),
    OR(".or"),
    UNTIL(".until");

    private final String string;

    Operator(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public TemporalMonitor<TrajectoryRecord, Double> createMonitor(List<Node<String>> siblings) {
        switch(this) {
            case NOT:
                return TemporalMonitor.notMonitor(STLFormulaMapper.parseSubTree(siblings.get(0)), new DoubleDomain());
            case OR:
                return TemporalMonitor.orMonitor(STLFormulaMapper.parseSubTree(siblings.get(0)), new DoubleDomain(),
                        STLFormulaMapper.parseSubTree(siblings.get(1)));
            default:
                //Perc startPerc = new Perc(siblings.get(2).getChildren());
                //Perc length = new Perc(siblings.get(3).getChildren());  // TODO: better fix issue of length can be == 0
                //Double start = startPerc.getValue();
                return TemporalMonitor.untilMonitor(STLFormulaMapper.parseSubTree(siblings.get(0)),
                        null, STLFormulaMapper.parseSubTree(siblings.get(1)),
                        //new Interval(start * 100.0, (start + (Math.max(0.1, length.getValue()) * (1.0 - start))) * 100.0), STLFormulaMapper.parseSubTree(siblings.get(1)),
                        new DoubleDomain());
        }
    }

}
