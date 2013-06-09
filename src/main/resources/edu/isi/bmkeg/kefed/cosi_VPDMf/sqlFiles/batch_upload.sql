LOAD DATA LOCAL INFILE 'SUB_FILEPATH_HERE/Journal.dat' REPLACE INTO TABLE Journal FIELDS TERMINATED BY '\t\t\t' LINES TERMINATED BY '\n\n\n' (vpdmfId,journalTitle,nlmId,abbr,ISSN);
LOAD DATA LOCAL INFILE 'SUB_FILEPATH_HERE/ViewTable.dat' REPLACE INTO TABLE ViewTable FIELDS TERMINATED BY '\t\t\t' LINES TERMINATED BY '\n\n\n' (vpdmfId,viewType,locked,vpdmfLabel,vpdmfUri,namespace,thumbnail);
