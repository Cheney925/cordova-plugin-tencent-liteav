#!/usr/bin/env bash

cd $(dirname $0)
cd ../..

if [ ! -e TXLiteAVSDK_Smart.framework ]; then unzip TXLiteAVSDK_Smart.framework.zip; fi
if [ -e TXLiteAVSDK_Smart.framework.zip ]; then rm TXLiteAVSDK_Smart.framework.zip; fi
