#!/bin/bash

read -p "Enter passowrd: " passvar
java -cp jasypt-1.9.2.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input=$passvar password=passphrase algorithm=PBEWithMD5AndDES
