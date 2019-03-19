package diffiehellman;

import java.util.Random;
import java.math.BigInteger;

public class DiffieHellman {
    public static void main(String[] args) {
        int KEY_SIZE = 64;
        BigInteger one = new BigInteger("1");
        BigInteger two = new BigInteger("2");

        BigInteger[] aValues = {
            new BigInteger("2"),
            new BigInteger("3"),
            new BigInteger("5"),
            new BigInteger("7"),
            new BigInteger("9"),
            new BigInteger("11"),
            new BigInteger("15"),
            new BigInteger("19")
        };

        // ---------------------------------------------------------------------
        // Generate a prime q and safe prime p
        // ---------------------------------------------------------------------
        boolean pIsPrime = false;
        BigInteger q = null;
        BigInteger p = null;

        while (!pIsPrime) {
            q = new BigInteger(KEY_SIZE, 20, new Random());

            p = q.multiply(two).add(one);
            BigInteger pMinusOne = p.subtract(one);
            pIsPrime = true; // Assume prime until proven otherwise

            // Test if p is prime with the test a values
            for (BigInteger a : aValues) {
                // If a^(p-1)mod(p) != 1, p is not a prime number
                if (a.modPow(pMinusOne, p).compareTo(one) != 0) {
                    pIsPrime = false;
                    break;
                }
            }
        }

        System.out.println("Value of q: " + q);
        System.out.println("Value of p: " + p);

        // ---------------------------------------------------------------------
        // Find a generator g
        // ---------------------------------------------------------------------
        BigInteger g = new BigInteger("2");
        boolean gIsGenerator = false;

        while (!gIsGenerator) {
            if (g.modPow(q, p).compareTo(one) != 0) {
                gIsGenerator = false;
                g = g.add(one);
            } else {
                gIsGenerator = true;
            }
        }

        System.out.println("Value of g: " + g);

        // ---------------------------------------------------------------------
        // Bob generates a secret b, and finds B=g^b mod(p)
        // ---------------------------------------------------------------------
        BigInteger b = new BigInteger(p.bitLength(), new Random());
        BigInteger B = g.modPow(b, p);
        System.out.println("\nBob's secret is b = " + b);
        System.out.println("Bob calculated B = " + B);

        // ---------------------------------------------------------------------
        // Alice generates a secret a, and finds A=g^a mod(p)
        // ---------------------------------------------------------------------
        BigInteger a = new BigInteger(p.bitLength(), new Random());
        BigInteger A = g.modPow(a, p);
        System.out.println("\nAlice's secret is a = " + a);
        System.out.println("Alice calculated A = " + A);

        System.out.println("\n---------- Alice and Bob share A and B ----------\n");

        // ---------------------------------------------------------------------
        // Bob receives A from Alice and finds K = A^b mod(p)
        // ---------------------------------------------------------------------
        BigInteger bob_K = A.modPow(b, p);
        System.out.println("Bob calculated K = " + bob_K);

        // ---------------------------------------------------------------------
        // Alice receives B from Bob and finds K = B^a mod(p)
        // ---------------------------------------------------------------------
        BigInteger alice_K = B.modPow(a, p);
        System.out.println("Alice calculated K = " + alice_K);


        // Check if K's match.
        if (bob_K.compareTo(alice_K) == 0) {
            System.out.println("\nAlice and Bob calculated the same K.");
        } else {
            System.out.println("The values of K did not match. Something went wrong.");
        }
    }
}
