#!/bin/bash

openssl aes-256-cbc -K "$enc_keystore_key" -iv "$enc_keystore_pass" -in "$1.enc" -out "$1" -d