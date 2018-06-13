# Codeplay

Run scheme code snippets by capturing its photo.

**Created for AngelHack 2018**


## Description
Aim of the idea is to move code from outside of the digital medium to digital medium seamlessly.
So that we can run, debug & validate the code effectively. For demonstration we are using scheme language


## Features
1. Works for handwritten code with variable accuracy.
   OCR is done using [Microsoft Azure service](https://azure.microsoft.com/en-in/blog/how-to-leverage-ocr-to-full-text-search-your-images-within-azure-search/)
2. Basic REPL features for scheme using [biwascheme](https://github.com/biwascheme/biwascheme)
   * Command execution window
   * Basic code editor
   * Syntax highlighting using [highlight.js](https://highlightjs.org/)
   * Partial support for Picture Language described in [sicp](http://sarabander.github.io/sicp/html/2_002e2.xhtml#g_t2_002e2_002e4)
3. Code can be saved and loaded

If you want to try it out, please set the azure API keys in the code

## TODO
* Support for other languages
* Improve character recognition
* Improve UX


## LICENSE
  Codeplay is licensed under the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
