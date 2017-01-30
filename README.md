# halftone-cryptography
Encodes images into shares using halftone visual cryptography. Both shares are needed to decode the image.

**Original Image**

<img src="https://dl.dropboxusercontent.com/s/dx90bksu3ihkts3/car.jpeg?dl=0" width="213" height="142" />

**Encoded Shares**

<img src="https://dl.dropboxusercontent.com/s/o26ruh20vwabeyw/share-0.png?dl=0" width="213" height="142" />
<img src="https://dl.dropboxusercontent.com/s/ykhaezaq0dolyt6/share-1.png?dl=0" width="213" height="142" />

**Decoded Image**

<img src="https://dl.dropboxusercontent.com/s/vfgcuk66pdsd647/combined.png?dl=0" width="213" height="142" />

##Quickstart##

##Build##
Download [the latest JAR](https://github.com/Zaloum/halftone-cryptography/raw/master/halftone-crypto.jar) or 
download the source files and build using Maven:

```mvn clean package```

##Run##

####Command Line####

```java -jar [jar-name].jar``` followed by user specified args

####Args####

```-encode [file name]``` encodes the specified file in to two shares

```-decode [share-1] [share-2]``` decodes the two shares

```-format [format-name]``` the output format (default: png)

```-out [name]``` the output file name

```-colors [color1] [color2]``` the foreground and background colors as either a java awt color or as a rgb combined integer (default: black, white)

##Examples##

Encodes the image _car.jpeg_ into two shares _car-share-0.bmp_, _car-share-1.bmp_.

```-encode car.jpeg -format bmp -out car-share```

Decodes the shares _car-share-0.bmp_, _car-share-1.bmp_ into the halftoned image _car-decoded.png_ with the object in the foreground 
being colored blue.

```-decode car-share-0.bmp car-share-1.bmp -out car-decoded -colors blue white```

##Issues##

Can throw an OutOfMemoryError for very large images. Can be fixed by assigning more memory to the JVM:

```java -Xmx1g -Xms1g -jar [jar-name].jar``` assigns 1gb of memory

##License##

[Public domain CC0 1.0](https://creativecommons.org/publicdomain/zero/1.0/)
