all: jar docs

docs: README.md src
	ant docs
	convert metroUMLdiagram.svg metroUMLdiagram.png
	pandoc -f markdown -t latex README.md -o design.pdf

jar: src lib
	ant
