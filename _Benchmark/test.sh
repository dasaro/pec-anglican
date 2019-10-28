#!/bin/sh
######################################################################
aux_folder="aux"
this_dir="_Benchmark"
installation_path=".."
translator="$installation_path/translator/linux_bin/translator";
#
last_instant=12
domain_description_folder="../Examples/AVATEA/Experiment3"
domain_description="$domain_description_folder/avatea.pec"
query_file="$domain_description_folder/query.lp"
#
report_dir="Reports/AVATEA/Experiment3"
report_file="taskCorrect.txt"
query="taskCorrect=true"
n_samples=1000
######################################################################

echo "Query: {$query}@I holds-with-prob P, for I=0,...,$last_instant."
echo "OPENING FILE $domain_description"
echo "WRITING TO $report_file in directory $report_dir. \n"
echo "Query: {$query}@I holds-with-prob P, for I=0,...,$last_instant. \n" > $report_dir/$report_file;
echo "Number of samples per query: $n_samples. \n" >> $report_dir/$report_file;
echo "Inst\tProb\tLoad.Time\tComp.Time" >> $report_dir/$report_file;

for i in $(seq 0 $last_instant)
do
	echo "{$query}@$i holds-with-prob P" > $query_file;
	echo "Executing query: {$query}@$i holds-with-prob P..."
	{ "$translator" < "$domain_description" > "$aux_folder/aux_domain.clj"; "$translator" -q < "$query_file" > "$aux_folder/aux_query.clj"; }
	{ cd "$installation_path" & lein run "$this_dir/$aux_folder/aux_domain.clj" -q "$this_dir/$aux_folder/aux_query.clj" -n "$n_samples"; } | awk -v i="$i" -F "[\[\]\(\)\ ]" 'BEGIN {count=0} /Elapsed/ { time[count]=$3; count++ } /true/ { if ($3 == "true") { prob = $4; } else { prob = $8; } } END { print i, "\t", prob, "\t", time[0], "\t" time[1] }' >> $report_dir/$report_file;

done

