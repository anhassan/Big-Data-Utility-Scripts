!/bin/bash

numArgs=3

usage() {
 echo " Usage: fsToHdfs.sh <src path> <dest path> <file name>"
 echo " src path: Local File System Path(Source)"
 echo " dest path: Hdfs File System Path(Destination)"
 echo " file name : Name of the file to be uploaded from local file system to Hdfs"
}

mkdirHdfs(){
 hadoop fs -mkdir -p /$1
 if [[ $? -ne 0 ]]
 then
    echo "Error: could not create directory: $1 in HDFS"
    exit 1
 fi
}

uploadHdfs(){
 hadoop fs -put $1 /$2
 if [[ $? -ne 0 ]]
 then
    echo "Error: could not upload file: $2 to HDFS $1"
    exit 1
 fi
}

if [ $# -eq $numArgs ]
then
    refSrcPath=$1
    hdfsDestPath=$2
    fileName=$3
    fullFilePath="${refSrcPath}/${fileName}"
else
    echo " Error: required the number of arguments to be $numArgs"
    usage
    exit 1
fi

if [[ -d "$refSrcPath" ]]
then
    echo "Uploading files in $refSrcPath to HDFS $hdfsDestPath ...."
    mkdirHdfs $hdfsDestPath 
else
    echo "Error: Directory $refSrcPath not found."
        exit 1
fi

if [[ -f "$fullFilePath" ]]
then 
    uploadHdfs $fullFilePath $hdfsDestPath
    echo "Uploaded $fullFilePath to HDFS $hdfsDestPath."
else
    echo "Error: File $fileName does not exist."
    exit 1
fi


