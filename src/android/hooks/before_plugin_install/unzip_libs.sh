#!/usr/bin/env bash

cd $(dirname $0)
cd ../..

if [ ! -e libs ]; then unzip libs.zip; fi
if [ -e libs.zip ]; then rm libs.zip; fi
