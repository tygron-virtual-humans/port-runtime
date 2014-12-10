#!/bin/bash

pathname=`dirname "$0"`
cd "$pathname"
ls

export Repast=~/repast-2.0.0-beta/
export RepastPlugins=$Repast/plugins/
export RepastRuntime=$RepastPlugins/repast.simphony.runtime_2.0.0
export RepastBinSrc=$RepastPlugins/repast.simphony.bin_and_src_2.0.0
export RepastCore=$RepastPlugins/repast.simphony.core_2.0.0
java -cp BW4TServer.jar\:$RepastCore/lib/\*\:$RepastRuntime/lib/\*\:$RepastRuntime/bin\:$RepastBinSrc/repast.simphony.bin_and_src.jar nl.tudelft.bw4t.server.BW4TEnvironment -scenario ./BW4T.rs -map Random -serverip localhost -serverport 8000

