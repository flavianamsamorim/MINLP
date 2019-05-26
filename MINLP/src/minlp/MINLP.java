/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minlp;

import java.io.PrintStream;
import java.util.Arrays;
import minlp.stream.Function;
import minlp.stream.Consumer;
import java.util.LinkedList;
import minlp.stream.Consumer2p;
import minlp.stream.Consumer3p;
import minlp.stream.Consumer4p;
import minlp.stream.Consumer5p;
import minlp.stream.Function2p;
import minlp.stream.Function3p;
import minlp.stream.Function4p;
import minlp.stream.Function5p;

/**
 * MINLP is the class to simplifying the programming of models for Java developers and allowing the same coded model to be solved by different solvers, it can be used to create and solve a large variety of Mathematical Programming models. Such models include:<br>
 * <ul>
 *  <li><b>LP</b> (All solvers)</li>
 *      linear programming models
 *  <li><b>MIP</b> (All solvers)</li> 
 *      mixed integer programming models
 *  <li><b>QP</b> (CPLEX only)</li>
 *      programs with quadratic terms in the objective function
 *  <li><b>QCP</b> (CPLEX only)</li>
 *      quadratically constrained programming models, including the special case of SOCP (second order cone programming)
 *  <li><b>MINLP</b> (All solvers)</li>
 *      some special cases of mixed integer non-linear programming models, this is the biggest effort of this proposal and MINLP will constantly propose to add new possibilities of coding and solving nonlinear functions in optimality . 
 * </ul>
 * MINLP takes the user definition and send to the specified solver, the solver stores such models internally in the standard math programming matrix representation like:<br>
 * <pre>
 *      Minimize (or Maximize)   c'x + x'Qx                                                         
 *      subject to               L &le Ax &le U                                                     
 *                       a<sub>i</sub>'x + x'Q<sub>i</sub> x &le r<sub>i</sub>  &nbsp &forall(i)   
 *                               l &le x &le u.                                                     
 * </pre>
 * Thus A is the matrix of linear constraint coefficients, and L and U are the vectors of lower and upper bounds on the vector of variables, x. 
 * The Q matrix must be positive semi-definite (or negative semi-definite in the maximization case) and represents the quadratic terms of the objective function. 
 * The matrices Q<sub>i</sub> must be positive semi-definite and represent the quadratic terms of the i-th quadratic constraint, and the a_i are vectors containing the corresponding linear terms. 
 * For more about the Q<sub>i</sub>, see the chapter about QCP in the CPLEX User's Manual.
 * If the model contains integer, Boolean, or semi-continuous variables, or if the model has special ordered sets (SOSs), the model is referred to as a mixed integer program (MIP). 
 * You can query whether the active model is a MIP with the method IloCplex.isMIP.  
 * A model with quadratic terms in the objective is referred to as a mixed integer quadratic program (MIQP) if it is also a MIP, and a quadratic program (QP) otherwise. 
 * You can query whether the active model has a quadratic objective by calling method  IloCplex.isQO.  
 * A model with quadratic constraints is referred to as a quadratically constrained program (MIQCP) if it is also a MIP, and as QCP otherwise. 
 * You can query whether the active model is quadratically constrained by calling the method  IloCplex.isQC.
 * A QCP may or may not have a quadratic objective; that is, a given problem may be both QP and QCP. Likewise, 
 * a MIQCP may or may not have a quadratic objective; that is, a given problem may be both MIQP and MIQCP.
 * If there are no quadratic terms in the objective, no integer constraints, and the problem is not quadratically constrained, it is called a linear program (LP).
 * If there are no quadratic terms in the objective, and the problem is not quadratically constrained, but there are integer variables, it is called a mixed integer linear program (MILP).
 * Special ordered sets (SOS) fall outside the conventional representation in terms of A and Q matrices and are stored separately.
 * <br>
 * <br>
 * To save time and has a self contained description much of the above description is an adaptation of the same description given on IloCplex solver documentation.
 * @see ilog.cplex.IloCplex
 * @author Marcio
 */
public abstract class MINLP {
    private final double M;    //big M

    protected int n_cols = 0;
    protected int n_rows = 0;
    public MINLP(double bigM) throws Exception {
        this.M = bigM;
    }

    /**
     * @return the number of variables on model
     * @throws Exception 
     */
    public abstract int getNcols() throws Exception;
    /**
     * @return the number of constraints on model
     * @throws Exception 
     */
    public abstract int getNrows() throws Exception;
    /**
     * @return the number of binary variables on model
     * @throws Exception 
     */
    public abstract int getNbinVars() throws Exception;
    /**
     * @return the number of integer variables on model
     * @throws Exception 
     */
    public abstract int getNintVars() throws Exception;
    
    /**
     * @param i
     * @return take and return the variable on i-th variable on model
     * @throws Exception 
     */
    public abstract Var getVar(int i)  throws Exception;

    /**
     * defines x &isin [lb, ub]
     * @param lb    the lower bound
     * @param ub    the upper bound 
     * @param name  variable name
     * @return      a continuous model variable
     * @throws Exception 
     */
    public abstract Var numVar(double lb, double ub, String name) throws Exception;

    /**
     * defines x &isin {0, 1} 
     * @param name  variable name
     * @return      a binary model variable
     * @throws Exception 
     */
    public abstract Var boolVar(String name) throws Exception;

    /**
     * defines x &isin [lb ... ub]
     * @param lb    the lower bound
     * @param ub    the upper bound 
     * @param name  variable name
     * @return      a integer model variable
     * @throws Exception 
     */
    public abstract Var intVar(int lb, int ub, String name) throws Exception;

    /**
     * Creates and returns an expression representing the product of a value and a numeric expression.
     * @param coef
     * @param expr
     * @return
     * @throws Exception 
     */
    
    public abstract Expr prod(double coef, Expr expr) throws Exception;
    /**
     * Creates and returns an expression representing the product of two integer expressions
     * @param expr1
     * @param expr2
     * @return
     * @throws Exception 
     */
    public abstract Expr prod(Expr expr1, Expr expr2) throws Exception;
    
    /**
     * Creates and returns an expression representing the sum of two numeric expressions.
     * @param expr1 The first numeric expression
     * @param expr2 The second numeric expression
     * @return      A numeric expression representing the sum of expr1 + expr2
     * @throws Exception 
     */
    public abstract Expr sum(Expr expr1, Expr expr2) throws Exception;
    
    /**
     * Creates and returns an objective to minimize the expression and adds it to the invoking model
     * @param obj   Expression to minimize.
     * @throws Exception 
     */
    public abstract void addMinimize(Expr obj) throws Exception;
    /**
     * Creates and returns an objective to maximize the expression and adds it to the invoking model
     * @param obj   Expression to maximize.
     * @throws Exception 
     */
    public abstract void addMaximize(Expr obj) throws Exception;
    
    /**
     * Creates and returns an expression representing a constant term
     * @param c A value for which to construct a constant expression term.
     * @return  An expression representing the constant c.
     * @throws Exception 
     */
    public abstract Expr constant(double c) throws Exception;

    /**
     * Creates and returns a constraint forcing the first specified numeric expression to be greater than 
     * or equal to the second specified numeric expression (to represent the constraint expr1 &ge expr2). 
     * The constraint is added to the invoking model; that is, this method modifies the current model. 
     * @param expr1 Lefthand side expression of the new greater-than-or-equal-to constraint.
     * @param expr2 Righthand side expression of the new greater-than-or-equal-to constraint.
     * @param name  name of this constraint
     * @throws Exception 
     */
    public abstract void addGe(Expr expr1, Expr expr2, String name) throws Exception;
     /**
     * Creates and returns a constraint forcing the first specified numeric expression to be less than 
     * or equal to the second specified numeric expression (to represent the constraint expr1 &le expr2). 
     * The constraint is added to the invoking model; that is, this method modifies the current model. 
     * @param expr1 Lefthand side expression of the new less-than-or-equal-to constraint.
     * @param expr2 Righthand side expression of the new less-than-or-equal-to constraint.
     * @param name  name of this constraint
     * @throws Exception 
     */
    public abstract void addLe(Expr expr1, Expr expr2, String name) throws Exception;
    /**
     * Creates and returns a constraint forcing the first specified numeric expression to be equal than 
     * or equal to the second specified numeric expression (to represent the constraint expr1 = expr2). 
     * The constraint is added to the invoking model; that is, this method modifies the current model. 
     * @param expr1 Lefthand side expression of the new equal-to constraint.
     * @param expr2 Righthand side expression of the new equal-to constraint.
     * @param name  name of this constraint
     * @throws Exception 
     */
    public abstract void addEq(Expr expr1, Expr expr2, String name) throws Exception;
    
    /**
     * <pre>Example:
     *      exportModel("myModel.lp");
     * </pre>
     * Writes the active model to the file specified by filename. 
     * The file format is determined by the extension of the filename.
     * The extensions avaiable will depend on each solver, see its documentation,
     * to specific extencions, to a easy way all solver suport .lp extension
     * @param filename  
     * @throws Exception 
     */
    public abstract void exportModel(String filename) throws Exception;

    /**
     * Solves the active model.
     * @return A Boolean value reporting whether a feasible solution has been found. This solution is not necessarily optimal. 
     * If false is returned, a feasible solution may still be present, but the solver has not been able to prove its feasibility.
     * @throws Exception 
     */
    public abstract boolean solve() throws Exception;
    
    /**
     * Specifie the time limit to solve the model
     * @param timeLimit time limit on seconds give to solve function
     * @throws Exception 
     */
    public abstract void setTimeLimit(double timeLimit) throws Exception;
    
    /**
     * Returns the solution status of the active model, some diferences on value returned cam ocurr for diferent solvers. The folowing values are common: <br>
     * <ul>
     * <li><b>Optimal</b> (All solvers)</li>
     * <li><b>Feasible</b> (All solvers)</li>
     * <li><b>Infeasible</b> (All solvers)</li>
     * <li><b>Unbounded</b> (All solvers)</li>
     * <li><b>Unknown</b> (Only Cplex)</li>
     * <li><b>INF_OR_UNBD</b> (Only Gurobi)</li>
     * <li><b>TIME_LIMIT</b> (Only Gurobi)</li>
     * <li><b>Undefined</b> (Only GLPK)</li>
     * </ul>
     * See the solvers documentation for more specifique informations.
     * @return one of the status above as string
     * @throws Exception 
     */
    public abstract String getStatus() throws Exception;

    /**
     * Returns the objective value of the current solution.
     * @return The objective value of the current solution
     * @throws Exception 
     */
    public abstract double getObjValue() throws Exception;
    
    /**
     * Returns the solution value for a variable.
     * @param var   The variable whose value is being queried. This variable must be in the active model
     * @return      The solution value for the variable var.
     * @throws Exception 
     */
    public abstract double getValue(Var var) throws Exception;

    /**
     * Returns the value that expr takes for the current solution.
     * @param expr  The expression for which to evaluate the current solution.
     * @return      The value expr takes for the current solution.
     * @throws Exception 
     */
    public abstract double getValue(Expr expr) throws Exception;

    /**
     * Releases the object and the associated objects created, release memory allocated on internal solvers.
     * @throws Exception 
     */
    public abstract void delete() throws Exception;

    /**
     * This method can behave differently on each solver
     * @see minlp.cplex.CPLEX#setOut(PrintStream)
     * @see minlp.gurobi.Gurobi#setOut(PrintStream)
     * @see minlp.glpk.GLPK#setOut(PrintStream)
     * @param stream
     * @throws Exception 
     */
    public abstract void setOut(PrintStream stream) throws Exception;

    /**
     * This method can behave differently on each solver
     * @see minlp.cplex.CPLEX#setWarning(PrintStream)
     * @see minlp.gurobi.Gurobi#setWarning(PrintStream)
     * @see minlp.glpk.GLPK#setWarning(PrintStream)
     * @param stream
     * @throws Exception 
     */
    public abstract void setWarning(PrintStream stream) throws Exception;
    
    /**
     * Creates and returns an expression representing the product of a value and a numeric expression.
     * @param expr
     * @param coef
     * @return
     * @throws Exception 
     */
    public final Expr prod(Expr expr, double coef) throws Exception{
        return prod(coef, expr);
    }
    
    
    /**
     * defines x<sub>i</sub> &isin [lb, ub] &nbsp &forall(i)
     * @param s     set of index
     * @param lb    lower bound
     * @param ub    upper bound
     * @param name  variable name
     * @return      a array of continuous variables
     * @throws Exception 
     */
    public final Var[] numVarArray(Set s, double lb, double ub, String name) throws Exception{
        Var array[] = new Var[s.size()];
        s.forAll(i -> {
            array[i] = numVar(lb, ub, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j</sub> &isin [lb, ub] &nbsp &forall(i,j)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param lb    lower bound
     * @param ub    upper bound
     * @param name  variable name
     * @return      a two dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][] numVarArray(Set s1, Set s2, double lb, double ub, String name) throws Exception{
        Var array[][] = new Var[s1.size()][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, lb, ub, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k</sub> &isin [lb, ub] &nbsp &forall(i,j,k)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param lb    lower bound
     * @param ub    upper bound
     * @param name  variable name
     * @return      a three dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][][] numVarArray(Set s1, Set s2, Set s3, double lb, double ub, String name) throws Exception{
        Var array[][][] = new Var[s1.size()][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, lb, ub, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k,m</sub> &isin [lb, ub] &nbsp &forall(i,j,k,m)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param s4    fourth set of index
     * @param lb    lower bound
     * @param ub    upper bound
     * @param name  variable name
     * @return      a four dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][][][] numVarArray(Set s1, Set s2, Set s3, Set s4, double lb, double ub, String name) throws Exception{
        Var array[][][][] = new Var[s1.size()][][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, s4, lb, ub, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k,m,n</sub> &isin [lb, ub] &nbsp &forall(i,j,k,m,n)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param s4    fourth set of index
     * @param s5    fifth set of index
     * @param lb    lower bound
     * @param ub    upper bound
     * @param name  variable name
     * @return      a five dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][][][][] numVarArray(Set s1, Set s2, Set s3, Set s4, Set s5, double lb, double ub, String name) throws Exception{
        Var array[][][][][] = new Var[s1.size()][][][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, s4, s5, lb, ub, name+"("+i+")");
        });
        return array;
    }
    
    
    /**
     * defines x<sub>i</sub> &isin {0, 1} 
     * @param name  variable name
     * @param s     a set of index
     * @return      a array of binary variable
     * @throws Exception 
     */
    public final Var[] boolVarArray(Set s, String name) throws Exception{
        Var array[] = new Var[s.size()];
        s.forAll(i -> {
            array[i] = boolVar(name+"["+i+"]");
        });
        return array;
    }
    /**
     * defines x<sub>i,j</sub> &isin {0, 1} 
     * @param name  variable name
     * @param s1    frist set of index
     * @param s2    second set of index
     * @return      a two dimencional array of binary variable
     * @throws Exception 
     */
    public final Var[][] boolVarArray(Set s1, Set s2, String name) throws Exception{
        Var array[][] = new Var[s1.size()][];
        s1.forAll(i -> {
            array[i] = boolVarArray(s2, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k</sub> &isin {0, 1} 
     * @param name  variable name
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @return      a three dimencional array of binary variable
     * @throws Exception 
     */
    public final Var[][][] boolVarArray(Set s1, Set s2, Set s3, String name) throws Exception{
        Var array[][][] = new Var[s1.size()][][];
        s1.forAll(i -> {
            array[i] = boolVarArray(s2, s3, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k,m</sub> &isin {0, 1} 
     * @param name  variable name
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param s4    forth set of index
     * @return      a four dimencional array of binary variable
     * @throws Exception 
     */
    public final Var[][][][] boolVarArray(Set s1, Set s2, Set s3, Set s4, String name) throws Exception{
        Var array[][][][] = new Var[s1.size()][][][];
        s1.forAll(i -> {
            array[i] = boolVarArray(s2, s3, s4, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k,m,n</sub> &isin {0, 1} 
     * @param name  variable name
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param s4    forth set of index
     * @param s5    fifth set of index
     * @return      a five dimencional array of binary variable
     * @throws Exception 
     */
    public final Var[][][][][] boolVarArray(Set s1, Set s2, Set s3, Set s4, Set s5,String name) throws Exception{
        Var array[][][][][] = new Var[s1.size()][][][][];
        s1.forAll(i -> {
            array[i] = boolVarArray(s2, s3, s4, s5, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i</sub> &isin [lb ... ub] &nbsp &forall(i)
     * @param s     set of index
     * @param lb    lower bound
     * @param ub    upper bound
     * @param name  variable name
     * @return      a array of integer variables
     * @throws Exception 
     */
    public final Var[] intVarArray(Set s, int lb, int ub, String name) throws Exception{
        Var array[] = new Var[s.size()];
        s.forAll(i -> {
            array[i] = intVar(lb, ub, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j</sub> &isin [lb ... ub] &nbsp &forall(i,j)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param lb    lower bound
     * @param ub    upper bound
     * @param name  variable name
     * @return      a two dimensional array of integer variables
     * @throws Exception 
     */
    public final Var[][] intVarArray(Set s1, Set s2, int lb, int ub, String name) throws Exception{
        Var array[][] = new Var[s1.size()][];
        s1.forAll(i -> {
            array[i] = intVarArray(s2, lb, ub, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k</sub> &isin [lb ... ub] &nbsp &forall(i,j,k)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param lb    lower bound
     * @param ub    upper bound
     * @param name  variable name
     * @return      a three dimensional array of integer variables
     * @throws Exception 
     */
    public final Var[][][] intVarArray(Set s1, Set s2, Set s3, int lb, int ub, String name) throws Exception{
        Var array[][][] = new Var[s1.size()][][];
        s1.forAll(i -> {
            array[i] = intVarArray(s2, s3, lb, ub, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k,m</sub> &isin [lb ... ub] &nbsp &forall(i,j,k,m)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param s4    fourth set of index
     * @param lb    lower bound
     * @param ub    upper bound
     * @param name  variable name
     * @return      a four dimensional array of integer variables
     * @throws Exception 
     */
    public final Var[][][][] intVarArray(Set s1, Set s2, Set s3, Set s4, int lb, int ub, String name) throws Exception{
        Var array[][][][] = new Var[s1.size()][][][];
        s1.forAll(i -> {
            array[i] = intVarArray(s2, s3, s4, lb, ub, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k,m,n</sub> &isin [lb ... ub] &nbsp &forall(i,j,k,m,n)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param s4    fourth set of index
     * @param s5    fifth set of index
     * @param lb    lower bound
     * @param ub    upper bound
     * @param name  variable name
     * @return      a five dimensional array of integer variables
     * @throws Exception 
     */
    public final Var[][][][][] intVarArray(Set s1, Set s2, Set s3, Set s4, Set s5, int lb, int ub, String name) throws Exception{
        Var array[][][][][] = new Var[s1.size()][][][][];
        s1.forAll(i -> {
            array[i] = intVarArray(s2, s3, s4, s5, lb, ub, name+"("+i+")");
        });
        return array;
    }
    
    /**
     * defines x &ge 0
     * @param name
     * @return
     * @throws Exception 
     */
    public final Var numVar(String name) throws Exception{
        return numVar(0, Double.POSITIVE_INFINITY, name);
    }
    /**
     * defines x<sub>i</sub> &ge 0 &nbsp &forall(i)
     * @param s     set of index
     * @param name  variable name
     * @return      a array of continuous variables
     * @throws Exception 
     */
    public final Var[] numVarArray(Set s, String name) throws Exception{
        
        Var array[] = new Var[s.size()];
        s.forAll(i -> {
            array[i] = numVar(0, Double.POSITIVE_INFINITY, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j</sub> &ge 0 &nbsp &forall(i,j)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param name  variable name
     * @return      a two dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][] numVarArray(Set s1, Set s2, String name) throws Exception{
        Var array[][] = new Var[s1.size()][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, 0, Double.POSITIVE_INFINITY, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k</sub> &ge 0 &nbsp &forall(i,j,k)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param name  variable name
     * @return      a three dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][][] numVarArray(Set s1, Set s2, Set s3, String name) throws Exception{
        Var array[][][] = new Var[s1.size()][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, 0, Double.POSITIVE_INFINITY, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k,m</sub> &isin [lb, ub] &nbsp &forall(i,j,k,m)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param s4    fourth set of index
     * @param name  variable name
     * @return      a four dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][][][] numVarArray(Set s1, Set s2, Set s3, Set s4, String name) throws Exception{
        Var array[][][][] = new Var[s1.size()][][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, s4, 0, Double.POSITIVE_INFINITY, name+"("+i+")");
        });
        return array;
    }
     /**
     * defines x<sub>i,j,k,m,n</sub> &isin [lb, ub] &nbsp &forall(i,j,k,m,n)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param s4    fourth set of index
     * @param s5    fifth set of index
     * @param name  variable name
     * @return      a five dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][][][][] numVarArray(Set s1, Set s2, Set s3, Set s4, Set s5, String name) throws Exception{
        Var array[][][][][] = new Var[s1.size()][][][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, s4, s5, 0, Double.POSITIVE_INFINITY, name+"("+i+")");
        });
        return array;
    }
    
    /**
     * defines x &isin [-&infin, +&infin]
     * @param name
     * @return
     * @throws Exception 
     */
    public final Var numVarFree(String name) throws Exception{
        return numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name);
    }
    /**
     * defines x<sub>i</sub> &isin [-&infin, +&infin] &nbsp &forall(i)
     * @param s     set of index
     * @param name  variable name
     * @return      a array of continuous variables
     * @throws Exception 
     */
    public final Var[] numVarArrayFree(Set s, String name) throws Exception{
        Var array[] = new Var[s.size()];
        s.forAll(i -> {
            array[i] = numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j</sub> &isin [-&infin, +&infin] &nbsp &forall(i,j)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param name  variable name
     * @return      a two dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][] numVarArrayFree(Set s1, Set s2, String name) throws Exception{
        Var array[][] = new Var[s1.size()][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k</sub> &isin [-&infin, +&infin] &nbsp &forall(i,j,k)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param name  variable name
     * @return      a three dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][][] numVarArrayFree(Set s1, Set s2, Set s3, String name) throws Exception{
        Var array[][][] = new Var[s1.size()][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name+"("+i+")");
        });
        return array;
    }
    /**
     * defines x<sub>i,j,k,m</sub> &isin [-&infin, +&infin] &nbsp &forall(i,j,k,m)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param s4    fourth set of index
     * @param name  variable name
     * @return      a four dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][][][] numVarArrayFree(Set s1, Set s2, Set s3, Set s4, String name) throws Exception{
        Var array[][][][] = new Var[s1.size()][][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, s4, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name+"("+i+")");
        });
        return array;
    }
     /**
     * defines x<sub>i,j,k,m,n</sub> &isin [-&infin, +&infin] &nbsp &forall(i,j,k,m,n)
     * @param s1    frist set of index
     * @param s2    second set of index
     * @param s3    third set of index
     * @param s4    fourth set of index
     * @param s5    fifth set of index
     * @param name  variable name
     * @return      a five dimensional array of continuous variables
     * @throws Exception 
     */
    public final Var[][][][][] numVarArrayFree(Set s1, Set s2, Set s3, Set s4, Set s5, String name) throws Exception{
        Var array[][][][][] = new Var[s1.size()][][][][];
        s1.forAll(i -> {
            array[i] = numVarArray(s2, s3, s4, s5, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name+"("+i+")");
        });
        return array;
    }
    
    /**<pre>
     * define: 
     *      v = linerizedProd(x,y)  &harr v = x*y
     * dedution: 
     *      if(y=1){
     *          v=x
     *      }else{ 
     *          v=0 
     *      }
     * linear transformation:
     *      let v &isin R
     *      M*(y-1) + x &le v &le x - M*(y-1)
     *      -M*y &le v &le M*y
     * where:
     *      M is a big positive constant (M &ge max(|ub(x)|,|lb(x)|) 
     * </pre>
     * @param expr a linear expression
     * @param y a boolean variable {0,1}
     * @return a new continous variable thats represent this linearization
     */
    public Var linerizedProd(Expr expr, Var y, String name) throws Exception{
        if(y.getLB()>1 || y.getUB()<0){
            throw new Exception("variable "+y.getName()+"must be boolean but has the bounds lb="+y.getLB()+" , ub="+y.getUB()); 
        }
        Var v = numVar(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name);
        //v <= x - M(y-1)
        addLe(v, sum(expr,prod(-M,sum(-1, y))));
        //v >= x + M(y-1)
        addGe(v, sum(expr,prod(+M,sum(-1, y))));
        //v <= +M*y
        addLe(v, prod(+M,y));
        //v >= -M*y
        addGe(v, prod(-M,y));
        return v;
    }
    /**
     * Generates a set of index {0,1,2, ..., n-1}
     * @param n number of total index
     * @return  
     * @throws Exception 
     */
    public final Set range(int n) throws Exception {
        return range(0, n-1);
    }
    /**
     * Generates a set of index {begin, begin+1, ..., end-1, end}
     * @param begin start index (inclusive)
     * @param end   start index (inclusive)
     * @return
     * @throws Exception 
     */
    public final Set range(int begin, int end) throws Exception {
        LinkedList<Integer> list = new LinkedList<>();
        for(int i=begin; i<=end; i++){
            list.addLast(i);
        }
        return new Set(this, list);
    }
    
    /**
     * <pre>
     * Interpratation:
     *      for all j in J {
     *          //do something here
     *      }
     * Simtax:
     *      cplex.forAll(J, (j)->{
     *          //do something here
     *      });
     * Sample:
     *      cplex.forAll(J, (j)->{
     *          cplex.addLe(x[j], 3);
     *      });
     * </pre>
     * @param set set of indexes
     * @param action the action to be performed
     * @throws java.lang.Exception 
     */
    public void forAll(Set set, Consumer action) throws Exception {
        set.forAll(action);
    }
    /**
     * <pre>
     * Interpratation:
     *      for all(i in I and j in J) {
     *          //do something here
     *      }
     * Simtax:
     *      cplex.forAll(I, J, (i, j)->{
     *          //do something here
     *      });
     * Sample:
     *      cplex.forAll(I, J, (i, j)->{
     *          cplex.addLe(x[i][j], 3);
     *      });
     * </pre>
     * @param s1 first set of index
     * @param s2 second set of index
     * @param action the action to be performed
     * @throws java.lang.Exception 
     */
    public void forAll(Set s1, Set s2, Consumer2p action) throws Exception {
        s1.forAll(i -> 
            s2.forAll(j -> 
                action.accept(i, j)
            )
        );
    }
    /**
     * <pre>
     * Interpratation:
     *      for all(i in I and j in J and k in K) {
     *          //do something here
     *      }
     * Simtax:
     *      cplex.forAll(I, J, K, (i, j, k)->{
     *          //do something here
     *      });
     * Sample:
     *      cplex.forAll(I, J, K, (i, j, k)->{
     *          cplex.addLe(x[i][j][k], 3);
     *      });
     * </pre>
     * @param s1 first set of index
     * @param s2 second set of index
     * @param s3 third set of index
     * @param action the action to be performed
     * @throws java.lang.Exception 
     */
    public void forAll(Set s1, Set s2, Set s3, Consumer3p action) throws Exception {
        s1.forAll(i -> 
            s2.forAll(j -> 
                s3.forAll(k -> 
                    action.accept(i, j, k)
                )
            )
        );
    }
    /**
     * <pre>
     * Interpratation:
     *      for all(i in I and j in J and k in K and m in M) {
     *          //do something here
     *      }
     * Simtax:
     *      cplex.forAll(I, J, K, M, (i, j, k, m)->{
     *          //do something here
     *      });
     * Sample:
     *      cplex.forAll(I, J, K, M, (i, j, k, m)->{
     *          cplex.addLe(x[i][j][k][m], 3);
     *      });
     * </pre>
     * @param s1 first set of index
     * @param s2 second set of index
     * @param s3 third set of index
     * @param s4 forth set of index
     * @param action the action to be performed
     * @throws java.lang.Exception 
     */
    public void forAll(Set s1, Set s2, Set s3, Set s4, Consumer4p action) throws Exception {
        s1.forAll(i -> 
            s2.forAll(j ->
                s3.forAll(k ->
                    s4.forAll(m ->
                        action.accept(i, j, k, m)
                    )
                )
            )
        );
    }
    /**
     * <pre>
     * Interpratation:
     *      for all(i in I and j in J and k in K and m in M and n in N) {
     *          //do something here
     *      }
     * Simtax:
     *      cplex.forAll(I, J, K, M, N, (i, j, k, m, n)->{
     *          //do something here
     *      });
     * Sample:
     *      cplex.forAll(I, J, K, M, N, (i, j, k, m, n)->{
     *          cplex.addLe(x[i][j][k][m][n], 3);
     *      });
     * </pre>
     * @param s1 first set of index
     * @param s2 second set of index
     * @param s3 third set of index
     * @param s4 forth set of index
     * @param s5 fifth set of index
     * @param action the action to be performed
     * @throws java.lang.Exception 
     */
    public void forAll(Set s1, Set s2, Set s3, Set s4, Set s5, Consumer5p action) throws Exception {
        s1.forAll(i -> 
            s2.forAll(j ->
                s3.forAll(k ->
                    s4.forAll(m ->
                        s5.forAll(n ->
                            action.accept(i, j, k, m, n)
                        )
                    )
                )
            )
        );
    }
    /**
     * <pre>
     * Interpratation:
     *      sum_{i in I}{expression}
     * Simtax:
     *      cplex.sum(I, i -> expression)
     * Sample:
     *      cplex.sum(I, i -> cplex.prod(C[i],x[i]))
     * </pre>
     * @param set
     * @param mapper
     * @return 
     * @throws java.lang.Exception 
     */
    public Expr sum(Set set, Function mapper) throws Exception {
        return set.sum(mapper);
    }
    /**
     * <pre>
     * Interpratation:
     *      sum_{i in I, j in J}{expression}
     * Simtax:
     *      cplex.sum(I, J, (i,j) -> expression)
     * Sample:
     *      cplex.sum(I, J, (i,j) -> cplex.prod(C[i][j],x[i][j]))
     * </pre>
     * @param s1 first set of index
     * @param s2 second set of index
     * @param mapper
     * @return 
     * @throws java.lang.Exception 
     */
    public Expr sum(Set s1, Set s2, Function2p mapper) throws Exception {
        return 
            s1.sum(i -> 
                s2.sum(j -> 
                    mapper.accept(i, j)
                )
            ); 
    }
    /**
     * <pre>
     * Interpratation:
     *      sum_{i in I, j in J, k in K}{expression}
     * Simtax:
     *      cplex.sum(I, J, K, (i,j,k) -> expression)
     * Sample:
     *      cplex.sum(I, J, K, (i,j,k) -> cplex.prod(C[i][j][k],x[i][j][k]))
     * </pre>
     * @param s1 first set of index
     * @param s2 second set of index
     * @param s3 third set of index
     * @param mapper
     * @return 
     * @throws java.lang.Exception 
     */
    public Expr sum(Set s1, Set s2, Set s3, Function3p mapper) throws Exception {
        return 
            s1.sum(i -> 
                s2.sum(j -> 
                    s3.sum(k -> 
                        mapper.accept(i, j, k)
                    )
                )
            ); 
    }
    /**
     * <pre>
     * Interpratation:
     *      sum_{i in I, j in J, k in K, m in M}{expression}
     * Simtax:
     *      cplex.sum(I, J, K, M, (i,j,k,m) -> expression)
     * Sample:
     *      cplex.sum(I, J, K, M, (i,j,k,m) -> cplex.prod(C[i][j][k][m],x[i][j][k][m]))
     * </pre>
     * @param s1 first set of index
     * @param s2 second set of index
     * @param s3 third set of index
     * @param s4 forth set of index
     * @param mapper
     * @return 
     * @throws java.lang.Exception 
     */
    public Expr sum(Set s1, Set s2, Set s3, Set s4, Function4p mapper) throws Exception {
        return 
            s1.sum(i -> 
                s2.sum(j -> 
                    s3.sum(k -> 
                        s4.sum(m -> 
                            mapper.accept(i, j, k, m)
                        )
                    )
                )
            ); 
    }
    /**
     * <pre>
     * Interpratation:
     *      sum_{i in I, j in J, k in K, m in M, n in N}{expression}
     * Simtax:
     *      cplex.sum(I, J, K, M, N, (i,j,k,m,n) -> expression)
     * Sample:
     *      cplex.sum(I, J, K, M, N, (i,j,k,m,n) -> cplex.prod(C[i][j][k][m][n],x[i][j][k][m][n]))
     * </pre>
     * @param s1 first set of index
     * @param s2 second set of index
     * @param s3 third set of index
     * @param s4 forth set of index
     * @param s5 fifth set of index
     * @param mapper
     * @return 
     * @throws java.lang.Exception 
     */
    public Expr sum(Set s1, Set s2, Set s3, Set s4, Set s5, Function5p mapper) throws Exception {
        return 
            s1.sum(i -> 
                s2.sum(j -> 
                    s3.sum(k -> 
                        s4.sum(m -> 
                            s5.sum(n -> 
                                mapper.accept(i, j, k, m, n)
                            )
                        )
                    )
                )
            ); 
    }
    /**
     * <pre>
     * Interpratation:
     *      sum_{i=start to end}{expression}
     * Simtax:
     *      cplex.sum(start, end, i -> expression)
     * Sample:
     *      cplex.sum(0, 5, i -> cplex.prod(C[i],x[i]))
     * </pre>
     * @param start begin index (inclusive)
     * @param end   end inex (inclusive)
     * @param mapper
     * @return 
     */
    public Expr sum(int start, int end, Function mapper)  throws Exception {
        return range(start, end).sum(mapper);
    }
    
    /**
     * Sum all array expression
     * @param array array of expressions
     * @return a expresion representing &sum<sub>i</sub>(array[i])
     * @throws java.lang.Exception 
     */
    public Expr sum(Expr... array)  throws Exception {
        return sum(0, array.length-1, (i)->array[i]);
    }
    /**
     * Sum a constant and all elements on array expression
     * @param d     the constant
     * @param array the array of expression
     * @return      a expresion representing d+&sum<sub>i</sub>(array[i])
     * @throws Exception 
     */
    public Expr sum(double d, Expr... array) throws Exception {
        return sum(constant(d), sum(array));
    }
    
    /**
     * Creates and returns a constraint that the first specified numeric expression must be greater than or equal to the second specified numeric expression.
     * @param expr1 
     * @param expr2 
     * @throws Exception 
     */
    public final void addGe(Expr expr1, Expr expr2) throws Exception{
        addGe(expr1, expr2, "r"+(n_rows+1));
    }
    /**
     * Creates and returns a constraint forcing the first specified numeric expression to be less than than or equal to the second specified numeric expression.
     * @param expr1
     * @param expr2
     * @throws Exception 
     */
    public final void addLe(Expr expr1, Expr expr2) throws Exception{
        addLe(expr1, expr2, "r"+(n_rows+1));
    }
    /**
     * Creates and returns a constraint initialized to represent the equality of the two specified expressions.
     * @param expr1
     * @param expr2
     * @throws Exception 
     */
    public final void addEq(Expr expr1, Expr expr2) throws Exception{
        addEq(expr1, expr2, "r"+(n_rows+1));
    }
    /**
     * Creates and returns a range representing the constraint that the specified numeric expression must be greater than or equal to the specified value.
     * @param expr
     * @param d
     * @throws Exception 
     */
    public final void addGe(Expr expr, double d) throws Exception{
        addGe(expr, constant(d));
    }
    /**
     * Creates and returns a range representing the constraint that the specified numeric expression must be less than or equal to the specified value.
     * @param expr
     * @param d
     * @throws Exception 
     */
    public final void addLe(Expr expr, double d) throws Exception{
        addLe(expr, constant(d));
    }
    /**
     * Creates and returns a range initialized to represent the equality of the specified expression and value.
     * @param expr
     * @param d
     * @throws Exception 
     */
    public final void addEq(Expr expr, double d) throws Exception{
        addEq(expr, constant(d));
    }

    /**
     * Defines w &asymp &radic(expr)<br><br>
     * Creates a non-linear cointinuous variable w using point fix representation to represents a aproximation for the square root of a give model expression
     * @param expr  expression to take the square root
     * @param lb    lower bound of square root
     * @param ub    upper bound of square root
     * @param nBits number of bits bo be used in the point fix represetation
     * @return      a non-linear object thats represent the result of this non-linear operation
     * @throws Exception 
     */
    public final nlVar addSqrt(Expr expr, double lb, double ub, int nBits) throws Exception {
        nlVar w = new nlVar(this, lb, ub, nBits, "w");
        Expr v = w.linerizedProd(w.value);
        addEq(expr, v);
        return w;
    }
    
    
    /**
     * Try take the current value of the variable, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double tryValue(Var var){
        try {
            return getValue(var);
        } catch (Exception ex) {
            return Double.NaN;
        }
    }
    /**
     * Try take the current value of the variable on array, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double[] tryValues(Var var[]){
        return Arrays.stream(var).mapToDouble(v->tryValue(v)).toArray();
    }
    /**
     * Try take the current value of the variable on array, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double[][] tryValues(Var var[][]){
        return Arrays.stream(var).map(v->tryValues(v)).toArray(double[][]::new);
    }
    /**
     * Try take the current value of the variable on array, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double[][][] tryValues(Var var[][][]){
        return Arrays.stream(var).map(v->tryValues(v)).toArray(double[][][]::new);
    }
    /**
     * Try take the current value of the variable on array, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double[][][][] tryValues(Var var[][][][]){
        return Arrays.stream(var).map(v->tryValues(v)).toArray(double[][][][]::new);
    }
    /**
     * Try take the current value of the variable on array, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double[][][][][] tryValues(Var var[][][][][]){
        return Arrays.stream(var).map(v->tryValues(v)).toArray(double[][][][][]::new);
    }

    /**
     * Get the current value of the variable on array, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double[] getValues(Var var[]){
        return tryValues(var);
    }
    /**
     * Get the current value of the variable on array, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double[][] getValues(Var var[][]){
        return tryValues(var);
    }
    /**
     * Get the current value of the variable on array, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double[][][] getValues(Var var[][][]){
        return tryValues(var);
    }
    /**
     * Get the current value of the variable on array, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double[][][][] getValues(Var var[][][][]){
        return tryValues(var);
    }
    /**
     * Get the current value of the variable on array, return NaN if the value is unknown
     * @param var
     * @return 
     */
    public final double[][][][][] getValues(Var var[][][][][]){
        return tryValues(var);
    }
    
    /**
     * Fix the array variables using its current model value
     * @param var   variables to be fixed
     * @throws Exception 
     */
    public final void fix(Var[] var) throws Exception {
        fix(var, tryValues(var));
    } 
    
    /**
     * Fix the array variables using specific values
     * @param var       variables to be fixed
     * @param values    values to fix
     * @throws Exception 
     */
    public void fix(Var[] var, double[] values) throws Exception {
        for(int i=0; i<values.length; i++){
            var[i].fix(values[i]);
        }
    }
}
