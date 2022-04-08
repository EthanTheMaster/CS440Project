# What is this?
This project was made by Ethan Lam and Patrick Oweijane. It is our final project for our CS 440 Algorithms course. In this project, we implemented the simplex algorithm to solve linear programming problems. We followed the presentation in Chapter 29 of _Introduction to Algorithms, 3rd Edition by Cormen, Leiserson, Rivest, Stein_ (CLRS). 

# Example 
This problem was adopted from _Mathematical Applications for the Management, Life, and Social Sciences (12th edition) by Harshbarger and Reynolds_.

Suppose a farm co-op plants either corn or soybeans on 6000 acres of land with the following resource constraints.

| | Corn | Soybeans | Available |
---|---|---|---|
Fertilizer/Herbicide | 9 gal/acres | 3 gal/acres | 40,500 gal |
Harvesting Labor | 3/4 hr/acres | 1 hr/acres | 5,250 hr |
Profit | 240 $/acre | 160 $/acre | |

Our goal is to optimize profit by planting corn and soybeans without violating resource constraints. Using our project, we can easily solve this optimization problem as follows: 

```java
LinearProgram p = new LinearProgram();
Variable corn = p.registerNonnegativeVariable("corn");
Variable soybeans = p.registerNonnegativeVariable("soybeans");

// Fertilizer/herbicide constraint
p.addConstraint(new Constraint(
        new ArrayList<>(Arrays.asList(corn, soybeans)),
        new ArrayList<>(Arrays.asList(9.0, 3.0)),
        Relation.LEQ,
        40500
));

// Labor constraint
p.addConstraint(new Constraint(
        new ArrayList<>(Arrays.asList(corn, soybeans)),
        new ArrayList<>(Arrays.asList(3.0/4.0, 1.0)),
        Relation.LEQ,
        5250
));

// Land constraint
p.addConstraint(new Constraint(
        new ArrayList<>(Arrays.asList(corn, soybeans)),
        new ArrayList<>(Arrays.asList(1.0, 1.0)),
        Relation.LEQ,
        6000
));

// Profit objective
p.setObjective(new ObjectiveFunction(
        ObjectiveGoal.MAXIMIZE,
        new ArrayList<>(Arrays.asList(corn, soybeans)),
        new ArrayList<>(Arrays.asList(240.0, 160.0))
));

System.out.println("Optimal profit: " + p.getObjectiveValue());
System.out.println("Corn: " + p.evaluateVariable(corn));
System.out.println("Soybeans: " + p.evaluateVariable(soybeans));
```