package com.wishPot;

public class A {
    public static void main(String[] args) {
        int n = 10; // Number of prime numbers to find
        int count = 0; // Count of primes found
        int num = 2; // Start checking for primes from 2

        while (count < n) {
            boolean isPrime = true;

            for(int i=2;i<=Math.sqrt(num);i++){
                if(num%i==0){
                    isPrime = false;
                    break;
                }
            }
            if(isPrime){
                System.out.println(num);
                count++;
            }
            num++;
        }
    }
}
