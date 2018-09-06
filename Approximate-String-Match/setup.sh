#!/usr/bin/env bash
mkdir typosys
cp -a ./Data ./typosys
cp typosys.java ./typosys
cd typosys
# First java file compile and run
javac typosys.java
# run test with weight [insertion deletion replace match]
# General GED with insertion and deletion share same penalty, replace, match is 0.
# (java typosys > ged_result.txt &) 
# This version consider replace as insertion plus deletion, and positive match <Baseline>
(java typosys -1 -1 -2 1 > ged_result1.txt &)
# This version put low penalty on deletion, so forgive type in more chars.
(java typosys -1 -1 -2 0 > ged_result2.txt &)
# This version put low penalty on insertion, so forgive miss a key stroke.
(java typosys -1 -2 -3 1 > ged_result3.txt &)
# This version put low penalty on deletion, so forgive type in more chars.
(java typosys -2 -1 -3 1 > ged_result4.txt &)
# All the processes above are experimenting on GED, with modified parameters to give different level of forgiveness.
cd ..
mkdir transpos
cp -a ./Data ./transpos
cp transpos.java ./transpos
cd transpos
#
javac transpos.java
# Damerau-Levenshtein Distance Standard (Transposition penalty in this program is 0 default as 1 to the original algorithm)
# (java transpos > dl_result.txt &)
# Double the edit distance of replace <Baseline>
(java transpos 1 1 2 -1 0 > dl_result1.txt &)
# Low match reward
(java transpos 1 1 2 0 0 > dl_result2.txt &)
# Low insert penalty
(java transpos 1 2 3 -1 0 > dl_result3.txt &)
# Low delete penalty
(java transpos 2 1 3 -1 0 > dl_result4.txt &)


exit 0