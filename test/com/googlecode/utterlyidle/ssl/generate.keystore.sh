#!/usr/bin/env bash

keytool -genkey -noprompt -trustcacerts -alias localhost -dname "cn=localhost" -validity 10000 -keypass password -keystore localhost.jks -storepass password