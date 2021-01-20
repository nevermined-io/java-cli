#!/usr/bin/env bash

ASCIIDOCTOR_FOLDER=target/generated-docs
DOCS_FOLDER=docs/commands

rm -rf $DOCS_FOLDER

for docbook in $ASCIIDOCTOR_FOLDER/*.xml; do
    if [ -f "$docbook" ]; then

        fileName=$(basename -- "$docbook")
#        fileNameWithoutExtension="$DOCS_FOLDER/${fileName%%.*}"
        if [[ "$fileName" == *"-"* ]]; then
          IFS='-'
          read -rasplitIFS<<< "$fileName"
          mkdir -p $DOCS_FOLDER/${splitIFS[0]} 2>1
          fileNameWithoutExtension="$DOCS_FOLDER/${splitIFS[0]}/${fileName%%.*}"
        else
          fileNameWithoutExtension="$DOCS_FOLDER/${fileName%%.*}"
        fi
        IFS=' '

        pandoc -f docbook -t gfm $docbook -o $fileNameWithoutExtension.md --columns=120
        iconv -t utf-8 $docbook | pandoc -f docbook -t gfm | iconv -f utf-8 | tee $fileNameWithoutExtension.md > /dev/null
        echo "Converted $docbook to Markdown: $fileNameWithoutExtension.md"
    fi
done
