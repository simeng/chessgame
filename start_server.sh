#!/bin/bash
if which rmiregistry; then
	pkill rmiregistry
	rmiregistry & 
else 
	echo 'Need rmiregistry in path' 
	exit 1
fi

java yacm.engine.boardgame.chess.ChessGame "$1"

