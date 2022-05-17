#include <pgmspace.h>


const char THINGNAME[] = "MyNewESP32";

const char WIFI_SSID[] = "GalaxyA21sCC42";
const char WIFI_PASSWORD[] = "ebfo8091";
const char AWS_IOT_ENDPOINT[] = "a2mr3kdz48c7g8-ats.iot.eu-central-1.amazonaws.com";

const char AWS_CERT_CA[] PROGMEM = R"EOF(
-----BEGIN CERTIFICATE-----
MIIDQTCCAimgAwIBAgITBmyfz5m/jAo54vB4ikPmljZbyjANBgkqhkiG9w0BAQsF
ADA5MQswCQYDVQQGEwJVUzEPMA0GA1UEChMGQW1hem9uMRkwFwYDVQQDExBBbWF6
b24gUm9vdCBDQSAxMB4XDTE1MDUyNjAwMDAwMFoXDTM4MDExNzAwMDAwMFowOTEL
MAkGA1UEBhMCVVMxDzANBgNVBAoTBkFtYXpvbjEZMBcGA1UEAxMQQW1hem9uIFJv
b3QgQ0EgMTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALJ4gHHKeNXj
ca9HgFB0fW7Y14h29Jlo91ghYPl0hAEvrAIthtOgQ3pOsqTQNroBvo3bSMgHFzZM
9O6II8c+6zf1tRn4SWiw3te5djgdYZ6k/oI2peVKVuRF4fn9tBb6dNqcmzU5L/qw
IFAGbHrQgLKm+a/sRxmPUDgH3KKHOVj4utWp+UhnMJbulHheb4mjUcAwhmahRWa6
VOujw5H5SNz/0egwLX0tdHA114gk957EWW67c4cX8jJGKLhD+rcdqsq08p8kDi1L
93FcXmn/6pUCyziKrlA4b9v7LWIbxcceVOF34GfID5yHI9Y/QCB/IIDEgEw+OyQm
jgSubJrIqg0CAwEAAaNCMEAwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMC
AYYwHQYDVR0OBBYEFIQYzIU07LwMlJQuCFmcx7IQTgoIMA0GCSqGSIb3DQEBCwUA
A4IBAQCY8jdaQZChGsV2USggNiMOruYou6r4lK5IpDB/G/wkjUu0yKGX9rbxenDI
U5PMCCjjmCXPI6T53iHTfIUJrU6adTrCC2qJeHZERxhlbI1Bjjt/msv0tadQ1wUs
N+gDS63pYaACbvXy8MWy7Vu33PqUXHeeE6V/Uq2V8viTO96LXFvKWlJbYK8U90vv
o/ufQJVtMVT8QtPHRh8jrdkPSHCa2XV4cdFyQzR1bldZwgJcJmApzyMZFo6IQ6XU
5MsI+yMRQ+hDKXJioaldXgjUkK642M4UwtBV8ob2xJNDd2ZhwLnoQdeXeGADbkpy
rqXRfboQnoZsG4q5WTP468SQvvG5
-----END CERTIFICATE-----
)EOF";

const char AWS_CERT_CRT[] PROGMEM = R"KEY(
-----BEGIN CERTIFICATE-----
MIIDWjCCAkKgAwIBAgIVAJe9rK5Bosek70FVBLyxKOWq4J3VMA0GCSqGSIb3DQEB
CwUAME0xSzBJBgNVBAsMQkFtYXpvbiBXZWIgU2VydmljZXMgTz1BbWF6b24uY29t
IEluYy4gTD1TZWF0dGxlIFNUPVdhc2hpbmd0b24gQz1VUzAeFw0yMjA1MTcxMDU1
NTJaFw00OTEyMzEyMzU5NTlaMB4xHDAaBgNVBAMME0FXUyBJb1QgQ2VydGlmaWNh
dGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDJXLqLf6KuMjxhAPhU
uy0bbnZuX3PYGj2W3r3Ch8P7ZQ8iF492tpdZgmScv0huTLktc2IEzsFA/q1gsYda
GgpkyN1xx626vA46wwDLw/g/KfHttiRYG/ng1N+w9NRpZEIUPnmEHVje7qWL3uG3
2ArsHFLTe+vdayPZtICQmP5f1vG01H3SCW5ArO0IG2m5we6ZewlVFAk0gk0nJg4A
rGgsnDxa+THnVbAuIUMZ0xa4z0II+YdXUxzpDR+jy64xpVca4Eq46E9YMt72Yt1h
qnmrYD57fC/1f2VjvuNVJtL1HWhrxkb3AJBFmL2fjnT+fF5yGLJy3iG+k94K0XiQ
IiyPAgMBAAGjYDBeMB8GA1UdIwQYMBaAFDj7L4zZjElqjJQL2AXhCLO1w48eMB0G
A1UdDgQWBBSZD7HGfiSzsUi8ry2HWYjfOultBjAMBgNVHRMBAf8EAjAAMA4GA1Ud
DwEB/wQEAwIHgDANBgkqhkiG9w0BAQsFAAOCAQEAmFDLgd4qQMxGEnlH6gUhEreU
nDy3jgDGgferG2gsxcpAWLn85MG+Jz7GK7Q3ZZVtLtumdk/lANqmQpqaX35Hxj8S
q4GbFCkwq/NNCs9Ce+Z/RjLlkiu7O23tU9YWlMLqyZdWx4rmxTSkXOSps37byHmX
SLmdpNoZbEWemNK6Hf2B4ErhgfcfNGvaQSAbOIyNy2/3aj1Fj8f9KcYJcQHqb4/C
/aqcVcU/ffxB3qCyOhJ6/l6HG+0yTncJZXK2N+/uR4sbK001IrF07mf7fItZAOYu
/ytsrAAgXjSfW/MvwwPIyYjKg7kLgZOlPmgRJs2VPrKSCRJpoW3qaTbCJQeD/A==
-----END CERTIFICATE-----
)KEY";


// Device Private Key
const char AWS_CERT_PRIVATE[] PROGMEM = R"KEY(
-----BEGIN RSA PRIVATE KEY-----
MIIEowIBAAKCAQEAyVy6i3+irjI8YQD4VLstG252bl9z2Bo9lt69wofD+2UPIheP
draXWYJknL9Ibky5LXNiBM7BQP6tYLGHWhoKZMjdcceturwOOsMAy8P4Pynx7bYk
WBv54NTfsPTUaWRCFD55hB1Y3u6li97ht9gK7BxS03vr3Wsj2bSAkJj+X9bxtNR9
0gluQKztCBtpucHumXsJVRQJNIJNJyYOAKxoLJw8Wvkx51WwLiFDGdMWuM9CCPmH
V1Mc6Q0fo8uuMaVXGuBKuOhPWDLe9mLdYap5q2A+e3wv9X9lY77jVSbS9R1oa8ZG
9wCQRZi9n450/nxechiyct4hvpPeCtF4kCIsjwIDAQABAoIBACNCjZfn3cDLIy+J
T/LmY9ut2Cczp8FrtuU4U9pAk9ztsVyy63hKOO5/msan6DMIVFTVI5Db85NCBXIt
JBpu/OrxEXXfFVjhnUxE4VrNEiddXPPXNT4IiBUKsbD27ksWN0v8NKMh0HIZL/vy
J2xd9Gur+gT2ajL3k+uHZAvt/n0hgtyZ+k2VaRQUmNaQDPKoxauht6BTtqa7t8y5
VwKwaZv98jSMeVAzGyrDgsXe7aRtDSUYbnyvWJjUV6/oW3lQqMY3CMTASOYnVjs9
IoMT4qKJptOzh7U7rvdyrIvYwOh0uUkodnVXrbo/WWbWO6DQAw10ylT+NyVBuJg7
xa/8O0ECgYEA6f+J9PeRR+i2WQUT0buuNnt+u/B1JI59lzEu3xLcgLKt6IAYwChc
Gc9mRmgXIKFIL83AbY8XOAvIH/OuLkE5mE/h+C9flSocexlXL+HRLjDZAAXntH4U
gs/JU/YI0ozJvnp49Jo3fe9/4qAh2VCsZTZm7Nm15xd4Eti1jrTORBkCgYEA3Euf
3NQNGp8KuT1G3lWSr4rFLMdSzZKaSuAUMFshocdkay1xlFoi01yHd585yTlJHFsK
+nAIvJ4pxa7dclbDrd1KMLlWdEI/hDJGLGEZrjOsbB1eVUJW5KZNzu+j6yTVL77C
T3eBtWe9lNB/YW7p2mqLI9/QmjrDrsbvnNtfyucCgYB5aftWHSLH0grTk9vdxmk1
ocx3jePqbPjje0SbwLijGpM8AUNWaVqolcG2pInjh2viSfsfndBpZ0uWsKTfIT7W
NhTE5yfMSQZwJQzlLmGQew4NggLg106jNVd8djvLq/t+FyQtnttOP/f2SipmDQL2
nUy4dpp45ueJt+I0wsIH+QKBgBi7rOEgNbBtdsjBZnmD5gorbGPQpS1LbmNFsT6U
nLwLJO0blwru68YLN3JowBYlhl3Wou7nMTPsFgiJz3cM4r9cfv3lcR1waq0u783/
wOBcENm6mmK8JgFZ4pZ/NUorQnwv0KUXZ8q0MQbKOeL4juzk1WZbtJH8UwgGcdSM
lL61AoGBAM/UMBWQBD6bts2ChZ1c1oG3CozvmWIl5ypNaUhpOq0o4tRdQ89KZt1Q
wisDpU/xQjGrsWwWHWHNLkTHkupvurB5BT1Yfuih32sDsWTu8cyEmdhKk1m7Ftr/
XHCeITUflmVUwI946eY4WkY4s8CmVDaWezhD/lipB4xodJzIw2Gs
-----END RSA PRIVATE KEY-----
)KEY";