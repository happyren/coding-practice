import subprocess
import random

#Given ax+by = gcd(a, b)
def extendedgcd(a, b):
	x, x_prev, y, y_prev = 0, 1, 1, 0
	r, r_prev = b, a #r and r_prev are remainder and previous remainder
	while r != 0:
		q = r_prev // r
		r_next = r_prev - q * r
		x_next = x_prev - q * x
		y_next = y_prev - q * y
		r_prev, r = r, r_next
		x_prev, x = x, x_next
		y_prev, y = y, y_next
	gcd = r_prev
	return gcd, x_prev

def modinverse(a, n):
	gcd, a_coe = extendedgcd(a, n)
	if gcd != 1:
		return "Modular inverse does not exist."
	else:
		return a_coe % n


prime_1 = int(subprocess.check_output(["openssl","prime","-generate","-bits","100"]))
prime_2 = int(subprocess.check_output(["openssl","prime","-generate","-bits","100"]))
n = prime_1 * prime_2

a_1 = int(subprocess.check_output(["openssl","rand","-hex","10"]),16)
a_2 = 10 * prime_1
a_3 = n-1

print "Prime 1 is: %d\nPrime 2 is: %d\nn is: %d" % (prime_1, prime_2, n)

print "a1 is %d, and modular inverse is: %s" % (a_1, modinverse(a_1,n))
print "a2 is %d, and modular inverse is: %s" % (a_2, modinverse(a_2,n))
print "a3 is %d, and modular inverse is: %s" % (a_3, modinverse(a_3,n))