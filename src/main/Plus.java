package main;

import java.util.List;

public class Plus extends Operation {

    public Plus() {
        this.name = "plus";
    }

    @Override
    public double calc(List<Double> list) {
        if(list.size() < 2) throw new ArithmeticException();
        return list.get(0) + list.get(1);
    }
}
