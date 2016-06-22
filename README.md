# SimpleFibonacci
Implementation of the Simple Fibonacci heap as described in "Fibonacci Heaps Revisited" by  H. Kaplan,  R.E. Tarjan and U. Zwick, 2014

Compared to the original Fibonacci heap as described in the paper "Fibonacci Heaps and Their Uses in Improved Network Optimization Algorithms" 
by M.L. Fredman and R.E. Tarjan. 1987. These heaps has only a single root, does cascading rank changes with just a single cut on decreaseKey operations, 
and the outcomes of all comparisons done by the algorithm are explicitly represented in the data structure. 

#Bounds:
###Insert: Θ(1) 
###FindMin: Θ(1)
###DeleteMin: O(log n)†
###Decrease-Key: Θ(1)†

† = amortized


The bounds are very similar to the original Fibonacci heap, however in the SFWithHeurisitcs implementation due to the Increasing rank heuristic,
the upper bound of the descrease key operation is O(log n) as opposed to O(n) elsewhere. 

#NOTE: 
As of now, June 2016, the SFPointerReduced Heap is not ready, we still have a few issues with the DecreaseKey operation. 
