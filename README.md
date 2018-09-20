# Java-Producer-Consumer
A solution to the Producer Consumer problem in java. 

# Problem specification. 

Write a console application which has N producers (N=1…10), M consumers (M=1…10) and one data queue. Each producer and consumer is a separate thread and all threads are working concurrently. Producer thread sleeps 0…100 milliseconds randomly then it wakes up and generates a random number between 1 and 100 and then puts this number to data queue.  Consumer thread sleeps 0…100 milliseconds randomly and then wakes up and takes the number from the queue and saves it to the output ‘data.txt’ file. All numbers are appended in the file and all they are comma delimited (for example 4,67,99,23,…). When producer thread puts the next number to data queue it checks the size of data queue, and if it is >=100 the producer thread is blocked until the number of elements gets <= 80. When consumer thread wants to take the next number from data queue and no elements in it, consumer thread is blocked until new element is added to data queue by a producer.
When we start application we need to insert the N (number of producers) and the M (number of consumers) after which program starts all threads. It should print current number of elements of data queue in each second. When we stop program it should interrupt all producers and wait for all consumers to save all queued data then program exits.
