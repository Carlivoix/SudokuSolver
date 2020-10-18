import java.util.ArrayList;
import java.util.List;

public class Case {
    private final int maxValue;
    private int value;
    private List<Integer> possible_values;
    private int nb_possible_values;
    private boolean done;

    public Case(int maxValue, int value){
        this.maxValue = maxValue;
        if (value != 0){
            this.setValue(value);
        } else {
            this.setDone(false);
            this.setAllPossible_values();
        }
    }

    public Case(int maxValue){ this(maxValue, 0); }

    public Case(Case c){
        this.maxValue = c.getMaxValue();
        if (c.getValue() != 0){
            this.setValue(c.getValue());
        }else{
            this.setDone(false);
            this.value = 0;
            this.nb_possible_values = c.getNb_possible_values();
            this.possible_values = new ArrayList<Integer>();
            this.possible_values.addAll(c.getPossible_values());
        }
    }

    public int getMaxValue(){ return this.maxValue; }

    public int getValue(){ return this.value; }

    public List<Integer> getPossible_values(){
        return this.possible_values;
    }

    public int getNb_possible_values(){
        return this.nb_possible_values;
    }

    public boolean getDone() { return this.done; }

    public void setValue(int value){
        this.value = value;
        this.setDone(true);
    }

    public void setDone(boolean done) { this.done = done; }

    public void setAllPossible_values(){
        this.possible_values = new ArrayList<Integer>();
        for (int i=0; i<this.getMaxValue(); i++){
            this.possible_values.add(i+1);
        }
        this.nb_possible_values = getMaxValue();
    }

    public void removePossible_value(int possible_value){
        this.possible_values.remove((Integer) possible_value);
        this.nb_possible_values -= 1;
    }

}
