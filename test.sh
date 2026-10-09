#!/usr/bin/env bash
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
cd "$HERE"
python3 -m unittest -v test_install.py
mkdir -p build/tests
javac -d build/tests src/com/jeremykenedy/splitflapdrift/SplitFlapOptions.java tests/SplitFlapOptionsTest.java
java -cp build/tests com.jeremykenedy.splitflapdrift.SplitFlapOptionsTest
