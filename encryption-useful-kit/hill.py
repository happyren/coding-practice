#!/usr/bin/env python

"""Decrypt a hill cipher with chosen plaintext attack.

Given the character space of 29 chars, applying chosen plaintext attack on hill cipher."""

from numpy import dot
from numpy import asarray
import numpy as np

__author__ = "Kaixiang Ren"
__copyright__ = "Copyright 2018."
__version__ = "1.0.1"
__email__ = "kaixiangr@student.unimelb.edu.au"

def getdet(mat):
    """This is a function to calculate determinant of a given matrix recursively."""

    size = len(mat)
    if(size<=2):
        return (mat[0][0]*mat[1][1]-mat[0][1]*mat[1][0])
    else:
        sign = 1
        col = 0
        sum = 0
        while col < size:
            temp = np.delete(mat,0,0)
            temp = np.delete(temp,col,1)
            sum += sign*mat[0][col]*getdet(temp)
            col += 1
            sign = -sign
        return sum

def extendedgcd(a, b):
    """This is a function to calculate extended gcd and return modular inverse if exist."""
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
    return x_prev

def adjMat(mat, i, j, mod):
    """This is a function to calculate partial adjugate matrix."""
    mat_adj = np.delete(mat,i,0)
    mat_adj = np.delete(mat_adj,j,1)
    det_adj = getdet(mat_adj) % 29
    return det_adj

def modInvMat(mat, mod):
    """This is main function to calculate modular inverse of a matrix."""
    size = len(mat)
    mat = np.array(mat) 
    det = getdet(mat)
    inv_det = extendedgcd(det, mod)
    mat_reserve = mat.copy()
    for i in range(size):
        for j in range(size):
            mat[j][i] = (-1)**(i+j)*adjMat(mat_reserve,i,j,mod) * inv_det % mod
    return mat

############################################### Example
charspace = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ,. '
charspace = map(None,charspace)
charspace = dict(zip(charspace,range(29)))
inv_charspace = {v: k for k, v in charspace.iteritems()}
chosenplain = [['C','T','R','L'],['C','A','P','S'],['H','O','M','E'],['P','G','U','P']]
chosencipher = [['J','Y','Z','P'],['Q','E','P','Q'],['C','H','Z','S'],['G','L','X','F']]
encrypt = 'BGB.D,LYIQJNBGSQLXWRIIBKESOWGEWSXCCAC.ZCPPW.YIAFPDNBUDOBPSFIKBSTRIQQFDOUHBZSRXVULMI,JVSGFUUG'
encrypt = map(None,encrypt)

for x, l in enumerate(chosenplain):
    for i, j in enumerate(l):
        l[i] = charspace.get(j)

for x, l in enumerate(chosencipher):
    for i, j in enumerate(l):
        l[i] = charspace.get(j)

chosenplain = np.matrix(chosenplain)
chosencipher = np.matrix(chosencipher)
inv_cipher = modInvMat(chosencipher, 29)
inv_key = dot(inv_cipher, chosenplain) % 29
key = modInvMat(inv_key, 29)
print key, inv_key

for i, l in enumerate(encrypt):
    encrypt[i] = charspace.get(l)

encrypt = [encrypt[i:i+4] for i in range(0, len(encrypt), 4)]

plain = dot(encrypt, inv_key)
plain = plain.tolist()

for j in plain:
    for i, l in enumerate(j):
        j[i] = inv_charspace.get(l % 29)

plain = ''.join(str(r) for v in plain for r in v)

print plain