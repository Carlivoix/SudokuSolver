import java.util.*;

public class Sudoku {
    private final int size;
    private final Case[][] cases;
    private final Hashtable<Integer, List<int[]>> possible_values;
    private int nb_done_cases;
    private List<Sudoku> solutions;
    private int nbSolutions;
    private boolean init;

    public Sudoku(int size){
        this.size = size;
        this.cases = new Case[(int)(Math.pow(size, 2))][(int)(Math.pow(size, 2))];
        this.possible_values = new Hashtable<Integer, List<int[]>>();
        this.nb_done_cases = 0;
        this.solutions = new ArrayList<Sudoku>();
        this.nbSolutions = 0;
        this.init = false;
        int max_index = (int)Math.pow(this.size, 2);
        for (int k=0; k<max_index+1; k++) {
            List<int[]> row_col_cases = new ArrayList<int[]>();
            this.possible_values.put(k, row_col_cases);
        }
        for (int i=0; i<Math.pow(size, 2); i++){
            for (int j=0; j<Math.pow(size, 2); j++){
                this.cases[i][j] = new Case((int)(Math.pow(size, 2)));
                addCaseInPossibleValues(i, j, max_index);
            }
        }
    }

    public Sudoku(Sudoku sudoku){
        this(sudoku.getSize());
        int len = (int)Math.pow(this.getSize(), 2);

        for (int row=0; row<len; row++){
            for (int col=0; col<len; col++){
                this.cases[row][col] = new Case(sudoku.getCases()[row][col]);
            }
        }
        this.possible_values.put(len, new ArrayList<int[]>());
        for (int possVal=0; possVal<len+1; possVal++){
            for(int idx=0; idx<sudoku.getPossible_values().get(possVal).size(); idx++){
                int[] caseRowCol = sudoku.getPossible_values().get(possVal).get(idx);
                this.addCaseInPossibleValues(caseRowCol[0], caseRowCol[1], possVal);
            }
        }
        this.nb_done_cases = sudoku.getNb_done_cases();
        this.solutions = sudoku.getSolutions();
        this.nbSolutions = sudoku.getNbSolutions();
        this.init = sudoku.isInit();
    }

    public void remember(Sudoku sudoku){
        int len = (int)Math.pow(this.getSize(), 2);
        for (int row=0; row<len; row++){
            for (int col=0; col<len; col++){
                this.cases[row][col] = new Case(sudoku.getCases()[row][col]);
            }
        }
        for (int possVal=0; possVal<len+1; possVal++){
            this.possible_values.put(possVal, new ArrayList<int[]>());
            for(int idx=0; idx<sudoku.getPossible_values().get(possVal).size(); idx++){
                int[] caseRowCol = sudoku.getPossible_values().get(possVal).get(idx);
                this.addCaseInPossibleValues(caseRowCol[0], caseRowCol[1], possVal);
            }
        }
        this.nb_done_cases = sudoku.getNb_done_cases();
    }

    public int getSize(){ return this.size; }
    public Case[][] getCases(){ return this.cases; }
    public Hashtable<Integer, List<int[]>> getPossible_values() { return this.possible_values; }
    public int getNb_done_cases() { return this.nb_done_cases; }
    public List<Sudoku> getSolutions() {return this.solutions; }
    public int getNbSolutions() { return this.nbSolutions; }
    public boolean isInit() { return this.init;}


    public void addCaseInPossibleValues(int row, int col, int nb_values){
        int[] row_col_case = {row, col};
        List<int[]> cases = this.possible_values.get(nb_values);
        cases.add(row_col_case);
        this.possible_values.put(nb_values, cases);
    }

    public void removeCaseInPossibleValues(int row, int col, int nb_values){
        int[] row_col_case = new int[]{row, col};
        List<int[]> cases = this.possible_values.get(nb_values);
        for (int idx=0; idx<cases.size();idx++){
            if (Arrays.equals(cases.get(idx), row_col_case)){
                cases.remove(idx);
            }
        }
        this.possible_values.put(nb_values, cases);
    }

    public void add_value(int row, int col, int value){ this.cases[row][col].setValue(value); }

    public boolean Done(){
        return this.nb_done_cases==(int)(Math.pow(this.size, 4));
    }

    public void compute_value(int row, int col, int value){
        int nb_values = this.cases[row][col].getNb_possible_values();
        this.removeCaseInPossibleValues(row, col, nb_values);
        // Computes the Case Objects in the same row
        for (int col_to_compute=0; col_to_compute<Math.pow(size, 2); col_to_compute++){
            if (col_to_compute==col || this.getCases()[row][col_to_compute].getDone() ) { continue; }
            if (this.getCases()[row][col_to_compute].getPossible_values().contains(value)){
                int nb_poss_values = this.getCases()[row][col_to_compute].getNb_possible_values();
                this.cases[row][col_to_compute].removePossible_value(value);
                this.removeCaseInPossibleValues(row, col_to_compute, nb_poss_values);
                this.addCaseInPossibleValues(row, col_to_compute, nb_poss_values-1);
            }
        }
        // Computes the Case Objects in the same column
        for (int row_to_compute=0; row_to_compute<Math.pow(size, 2); row_to_compute++){
            if (row_to_compute==row || this.cases[row_to_compute][col].getDone() ) { continue; }
            if (this.getCases()[row_to_compute][col].getPossible_values().contains(value)){
                int nb_poss_values = this.getCases()[row_to_compute][col].getNb_possible_values();
                this.cases[row_to_compute][col].removePossible_value(value);
                this.removeCaseInPossibleValues(row_to_compute, col, nb_poss_values);
                this.addCaseInPossibleValues(row_to_compute, col, nb_poss_values-1);
            }
        }
        // Computes the Case Objects in the same box
        int[] rows_in_box = indices_same_box(row, size);
        int[] cols_in_box = indices_same_box(col, size);
        for (int row_to_compute: rows_in_box){
            for (int col_to_compute: cols_in_box){
                if (row_to_compute==row && col_to_compute==col ||
                        this.getCases()[row_to_compute][col_to_compute].getDone()) { continue; }
                if (this.getCases()[row_to_compute][col_to_compute].getPossible_values().contains(value)){
                    int nb_poss_values = this.getCases()[row_to_compute][col_to_compute].getNb_possible_values();
                    this.cases[row_to_compute][col_to_compute].removePossible_value(value);
                    this.removeCaseInPossibleValues(row_to_compute, col_to_compute, nb_poss_values);
                    this.addCaseInPossibleValues(row_to_compute, col_to_compute, nb_poss_values-1);
                }
            }
        }
        this.nb_done_cases += 1;
    }

    public boolean noSolution(){
        return !this.getPossible_values().get(0).isEmpty();
    }

    public void initialize(){
        int length = (int)(Math.pow(this.size, 2));
        for (int row=0; row<length; row++){
            for (int col=0; col<length; col++){
                if (this.getCases()[row][col].getDone()){
                    this.compute_value(row, col, this.getCases()[row][col].getValue());
                }
            }
        }
        this.init = true;
    }

    public int nextCaseNbPossVal(){
        int nb_possible_values = 1;
        while (nb_possible_values<Math.pow(this.getSize(), 2)+1){
            if (!this.getPossible_values().get(nb_possible_values).isEmpty()){
                return nb_possible_values;
            }
            nb_possible_values += 1;
        }
        return 0;
    }

    public int[] nextCase_rowcol(){
        int nbPossVal = this.nextCaseNbPossVal();
        int range = this.getPossible_values().get(nbPossVal).size();
        int idx_next_case = (int)(Math.random()*range);
        return this.getPossible_values().get(nbPossVal).get(idx_next_case);
    }

    public void solve(int verbose, int maxNbSolToFind){
        if (verbose ==1) {
            System.out.println(this);
            System.out.println("\n Starting next step");
        }
        if (this.getNbSolutions()==maxNbSolToFind){
            return;
        }
        if (this.Done()){
            this.solutions.add(new Sudoku(this));
            this.nbSolutions += 1;
            System.out.println("A solution has been found \n");
            System.out.println("Since the beginning, "+this.getNbSolutions()+" solution have been found.");
            return;
        }
        if (this.noSolution()){
            if (verbose ==1) {
                System.out.println("There is no solution there \n");
            }
            return;
        }
        if (!this.isInit()){
            if (verbose ==1) {
                System.out.println("Initializing the instance");
            }
            this.initialize();
        }
        int nextCaseNbPossVal = this.nextCaseNbPossVal();
        int[] nextCaseRowCol = this.nextCase_rowcol();
        int nextCaseRow = nextCaseRowCol[0];
        int nextCaseCol = nextCaseRowCol[1];
        if (nextCaseNbPossVal==1) {
            if (verbose ==1) {
                System.out.println("There is one choice for the next value \n");
            }
            int nextCaseVal = this.getCases()[nextCaseRow][nextCaseCol].getPossible_values().get(0);
            this.add_value(nextCaseRow, nextCaseCol, nextCaseVal);
            this.compute_value(nextCaseRow, nextCaseCol, nextCaseVal);
            this.solve(verbose, maxNbSolToFind);
        }else{
            if (verbose ==1) {
                System.out.println("There is no certain next value");
                System.out.println("Making an assumption on the next value \n");
            }
            List<Integer> nextCasePossVal = this.getCases()[nextCaseRow][nextCaseCol].getPossible_values();
            Collections.shuffle(nextCasePossVal);
            Sudoku partialSudoku = new Sudoku(this);
            for (int possVal: nextCasePossVal){
                this.remember(partialSudoku);
                this.add_value(nextCaseRow, nextCaseCol, possVal);
                this.compute_value(nextCaseRow, nextCaseCol, possVal);
                this.solve(verbose, maxNbSolToFind);
            }
        }
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        int max_size = Double.toString(Math.pow(this.size, 2)).length();
        int len = (int)Math.pow(this.size, 2);
        for (int i=0; i<len; i++){
            for (int j=0; j<len; j++){
                if (this.cases[i][j].getDone()) {
                    str.append(" ");
                    str.append(" ".repeat(Math.max(0, max_size - 1)));
                    str.append(this.cases[i][j].getValue());
                    str.append(" ");
                } else {
                    str.append(" ".repeat(Math.max(0, max_size - 1)));
                    str.append(" - ");
                }
                if (j>0 && j<len-1 && (j+1)%this.size==0){
                    str.append(" | ");
                }
                if (j==len-1) {
                    if (i>0 && i<len-1 && (i+1)%this.size==0){
                        str.append("\n ");
                        str.append("_".repeat((max_size+2)*len+3*(this.size-1)));
                    }
                    str.append("\n");
                }
            }
        }
        return str.toString();
    }

    public static int[] indices_same_box(int indice, int size){
        int[] indices = new int[size];
        int box = indice/size;
        for (int i=0; i<size; i++){
            indices[i] = box*size + i;
        }
        return indices;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Sudoku sudo = new Sudoku(4);
        sudo.solve( 0, 10000);
        System.out.println(sudo.nbSolutions + " solutions have been found");
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("The code was executed in: " + timeElapsed/1000 + " seconds");
    }
}
