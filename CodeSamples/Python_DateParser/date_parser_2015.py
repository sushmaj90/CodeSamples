import dpla_utils
import profiler_config as config
import re
import json
import copy
import datetime

# DPLA date parser to categorize and process sourceResource.date.displayDate based on their formats
# 1.Unknown/Image/Donated
# 2.BCE dates
# 3.Century/Cent/c
# 4.Dates with month names
# 5.Dates with mm/dd/yyyy or mm/yyyy format
# 6.Dates without BCE or Century formats:
#       a.Single block of dates
#       b.Dates with Ranges
# 7.Unusual Date formats that require DPLA attention
collection_id = "3f0e282f7fed21d7790fce877faf11d7" #Artstor collection id
datesToProcessList = [] #List of dates that needs to be categorized
duplicatedDates = [] #List of duplicated dates from DPLA
unknownDateList = [] #List to store Format 1 dates
bceList = [] #List to store Format 2 dates
dayRangeWithMonth = [] #List to store dates that containg day ranges in Format 4
listWithMonthPatterns = [] #List to store Format 4 dates
beginDate = [] #List to store begin dates of items in a collection
endDate = [] #List to store end dates of item in collection
centList = [] #List to store Format 3 dates
datesWithmmddyyList =[] #List to store Format 5 dates
datesWithHyphen = [] #List to stote Format 6b dates
singleDatesList = [] #List to stote Format 6a dates
dplaAttention =[] #List to store Format 7 dates
processedList = [] #List to store dates after processing
processedDict ={} #Dict that stores all the Processed dates in the form of before processing and after processing
processedList1 =[] #supplementary List to store dates before processing
processedList2 =[] #supplementary List to store dates after processing
DateRange =[] #List to store the processed date ranges
item = {}
item_to_process = {}
Month_list = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"]
BCE_word = ['BCE', 'BC', 'bce', 'bc']
unknown_word = ['None', 'none', 'unknown', 'Unknown', 'Image', 'image', 'Donated', 'donated']
image_words = ['Image', 'image']
cent_word = ["Century", "Cent", "cent", "century"]
condition = {'sourceResource.collection.id': collection_id, 'fields':'sourceResource.date.displayDate'}
count_item = dpla_utils.dpla_get_count(True, api_key=config.API_KEY, **condition)
print("Total item:", count_item)
page_size = 500

total_num_pages = int(count_item / page_size) + 1
print('Total Pages:', total_num_pages)
for i in range(1, total_num_pages + 1, 1):
        print('processing page', i)
        condition['page_size'] = page_size
        condition['page'] = i
        dpla_response = dpla_utils.dpla_fetch(True, count=page_size, api_key=config.API_KEY, **condition)
        docs = dpla_response

        for doc in docs:
            if bool(doc):
                if doc['sourceResource.date.displayDate'] in datesToProcessList:
                    duplicatedDates.append(doc['sourceResource.date.displayDate'])
                else:
                    date_to_process = doc['sourceResource.date.displayDate']
                    datesToProcessList.append(date_to_process)

# Grouping Format 1 dates
idListUnknownCopy = copy.deepcopy(datesToProcessList)
for unKnownString in idListUnknownCopy:
    foundUnknown = False
    for unknownWord in unknown_word:
        if unknownWord in unKnownString and foundUnknown ==False:
            unknownDateList.append(unKnownString)
            foundUnknown = True
            datesToProcessList.remove(unKnownString)
# Groupinf Format 2 dates
idListCopy = copy.deepcopy(datesToProcessList)
for dateValue in idListCopy:
     found = False
     for bcePattern in BCE_word:
        if bcePattern in dateValue and found == False:
            bceList.append(dateValue)
            found = True
            datesToProcessList.remove(dateValue)
# Grouping Format 3 dates
idListCenturyCopy = copy.deepcopy(datesToProcessList)
for centValue in idListCenturyCopy:
     foundCent = False
     for centPattern in cent_word:
        if centPattern in centValue and foundCent == False:
            centList.append(centValue)
            foundCent = True
            datesToProcessList.remove(centValue)
#Grouping Format 3 with 'c' as century
idListCentWithC = copy.deepcopy(datesToProcessList)
for centc in idListCentWithC:
    if re.search('c{1}\.?\s\D(CE)?',centc):
        centList.append(centc)
        datesToProcessList.remove(centc)
# Grouping Format 4 dates
idListCopyMonths = copy.deepcopy(datesToProcessList)
for monthOccurence in idListCopyMonths:
    foundMonth = False
    for month in Month_list:
        if re.search(month, monthOccurence, re.IGNORECASE) and foundMonth == False:
            listWithMonthPatterns.append(monthOccurence)
            foundMonth = True
            datesToProcessList.remove(monthOccurence)
# Processing Group 4 dates
# Searches for 3 or 4 block of digits (year) initially from Group4. If occurence is found, begin date and end date is calculated
# else look for formats like 01-Oct-03 and 1900 November 05-09
for monthOccurence in listWithMonthPatterns:
    if re.search('\A\d{1,2}[-]\w+\Z',monthOccurence) or re.search('\A\w+\s\d{1,2}\Z',monthOccurence) or re.search('\A\w+\s\d{1,2}\s+[-]\s+\w+\s\d{1,2}\Z',monthOccurence):
        dplaAttention.append(monthOccurence)
    else:
        findYearInMonthString = re.findall("\d{3,4}", monthOccurence)
        if( not findYearInMonthString):
            ddmmyyFormat = re.search('\A\d{1,2}[-]\w{3}[-]\d{1,4}\Z', monthOccurence) #01-Oct-03
            if ddmmyyFormat:
                extractYear = datetime.datetime.strptime(monthOccurence,"%d-%b-%y")
                processedDate = extractYear.year
            elif(re.search('\d{1,4}\s\w{3,9}\s\d{1,2}[-]\d{1,2}', monthOccurence)): #1900 November 05-09
                monthWithDayRange = monthOccurence.split(" ")
                for eachMonthWithDayRange in monthWithDayRange:
                    if '-' in eachMonthWithDayRange or eachMonthWithDayRange.isalpha():
                        dayRangeWithMonth.append(eachMonthWithDayRange)
                    else:
                        extractYear = datetime.datetime.strptime(eachMonthWithDayRange,"%Y")
                        processedDate = extractYear.year
            beginDate.append(processedDate)
            endDate.append(processedDate)
            processedList1.append(monthOccurence)
            processedList2.append(str(processedDate)+"-"+str(processedDate))
        else:
            if len(findYearInMonthString) > 1:
                beginDate.append(int(findYearInMonthString[0]))
                endDate.append(int(findYearInMonthString[1]))
                processedDate = findYearInMonthString[0]+"-"+findYearInMonthString[1]
            else:
                beginDate.append(int(findYearInMonthString[0]))
                endDate.append(int(findYearInMonthString[0]))
                processedDate = findYearInMonthString[0]+"-"+findYearInMonthString[0]
            processedList1.append(monthOccurence)
            processedList2.append(processedDate)

#Grouping Format 5 dates
idListddmmyyFormat = copy.deepcopy(datesToProcessList)
for dates in idListddmmyyFormat:
    if re.search('\A\d{1,2}/\d{1,2}/\d{1,4}\Z', dates) or re.search('\A\d{1,2}/\d{1,4}\Z', dates) or re.search('\A\d{1,2}/\d{1,4}\Z', dates):
        datesWithmmddyyList.append(dates)
        datesToProcessList.remove(dates)
#Processing Format 5 dates for following sub-categories: month/day/yeat or day/month/year or month/year
for datesWithmmddyy in datesWithmmddyyList:
    if re.search('\A\d{1,2}/\d{1,2}/\d{1,4}\Z', datesWithmmddyy):
        arrayWithMonthAndDay = datesWithmmddyy.split('/')
        if len(arrayWithMonthAndDay[2]) > 2: #To extract year with a century #01/10/2003
            if arrayWithMonthAndDay[1] >12:
                extractYear = datetime.datetime.strptime(datesWithmmddyy,"%m/%d/%Y")
            else:
                extractYear = datetime.datetime.strptime(datesWithmmddyy,"%d/%m/%Y")
            processedDate = extractYear.year
        else: #To extract year without four digits (Example: 01/10/35
            if arrayWithMonthAndDay[1] >12:
                extractYear = datetime.datetime.strptime(datesWithmmddyy,"%m/%d/%y")
            else:
                extractYear = datetime.datetime.strptime(datesWithmmddyy,"%d/%m/%y")
            processedDate = extractYear.year
    elif re.search('\A\d{1,2}/\d{1,4}\Z', datesWithmmddyy):
        extractYear = datetime.datetime.strptime(datesWithmmddyy,"%m/%Y")
        processedDate = extractYear.year
    elif re.search('\A\d{1,2}/\d{1,4}\Z', datesWithmmddyy):
        extractYear = datetime.datetime.strptime(datesWithmmddyy,"%Y/%m")
        processedDate = extractYear.year
    processedList1.append(datesWithmmddyy)
    processedList2.append(str(processedDate)+"-"+str(processedDate))
    beginDate.append(processedDate)
    endDate.append(processedDate)
#Grouping and Processing Format 6 dates
idListSingleDigits = copy.deepcopy(datesToProcessList)
for dates in idListSingleDigits:
    if re.search('-', dates):
        datesWithHyphen.append(dates)
        datesToProcessList.remove(dates) #Grouping Format 6b
    elif re.search('\d{1,4}',dates): #Grouping Format 6a
        findSingleBlock = re.findall('\d{1,4}', dates)
        if len(findSingleBlock) == 1:
            beginDate.append(int(findSingleBlock[0]))
            endDate.append(int(findSingleBlock[0]))
            processedList1.append(dates)
            processedList2.append(findSingleBlock[0]+"-"+findSingleBlock[0])
            singleDatesList.append(dates)
        else:
            dplaAttention.append(dates) #Add the remaining dates to dplaAttention list
        datesToProcessList.remove(dates)
    else:
        dplaAttention.append(dates) #Add the patterns not picked to dplaAttention list

# Forming a dictionary of the form BeforeProcessing:AfterProcessing
for i in range(len(processedList1)):
    processedDict[processedList1[i]] = processedList2[i]
processedList.append(processedDict)
# Forming a dictionary of date ranges in the form BeginDate-EndDate
for i in range(len(beginDate)):
    DateRange.append(str(beginDate[i])+"-"+str(endDate[i]))

item["CenturyDates"] = centList
item["UnknownDates"] = unknownDateList
item["BceDates"] = bceList
item["DatesWithMonthNames"] = listWithMonthPatterns
item["DatesWithHyphen"] = datesWithHyphen
item["BeginDate"] = beginDate
item["EndDate"] = endDate
item["Yearwithmmddyyyy"] = datesWithmmddyyList
item["SingleBlockOfDates"] = singleDatesList
item["RequiresDPLAAttention"]=dplaAttention
item["ProcessedDates"] = processedList
item["DateRanges"] = DateRange
dest_file = open('ProcessedDates.json', 'w')
dest_file.write(json.dumps(item))
dest_file.close()



