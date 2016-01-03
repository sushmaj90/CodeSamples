# Main Program - test_aapl.py
#Code to scrape the data from yahoo options web page using python and beautifulsoup
from bs4 import BeautifulSoup
import json
from operator import itemgetter
import sys
import re
import urllib
import string
import ast

html_doc = ""
def contractAsJson(filename):
 file_split = filename.split('.')
 file_subname = file_split[0]
 my_file_name = filename
 my_file = open(my_file_name)
 html_doc = my_file.read()

 soup = BeautifulSoup(html_doc,'html.parser')
 currentprice = soup.find(id = "yfs_l84_"+file_subname).string
 url_links = soup.find_all('a', href=True)
 url_links_new = []
 matched_urls = []
 replaced_urls = []
 return_urls = []
 contracts_list = []
 rows_list = []
 cols_list = []
 symbol_list = []
 open_list = []
 chg_list = []
 last_list = []
 ask_list = []
 vol_list = []
 open_list = []
 strike_list = []
 bid_list= []
 symbol_type = []
 sticker_symbol = []
 Date = []
 for link in url_links:
     url_links_new.append(link["href"])
 file_in_url = file_subname.upper()
 for url_object in url_links_new:
  pattern_match = re.search("/q/op\?s="+file_in_url+"&m=[0-9 \-]*",url_object)
  if pattern_match:
      matched_urls.append(url_object.encode('ascii','ignore'))
 for url_object2 in url_links_new:
  pattern_match2 = re.search("/q/os\?s="+file_in_url+"&m=",url_object2)
  if pattern_match2:
      matched_urls.append(url_object2.encode('ascii','ignore'))

 for matched_url in matched_urls:
     replaced_urls.append(string.replace(matched_url,'&','&amp;'))
 for replaced_url in replaced_urls:
     return_urls.append("http://finance.yahoo.com"+replaced_url)

 contracts_data = soup.findAll('table', attrs = {'class':'yfnc_datamodoutline1'})
 for lines_contract_data in contracts_data:
     rows_list.append(lines_contract_data.find_all('tr'))
 call_table = rows_list[0]
 put_table = rows_list[1]
 del call_table[:2]
 del put_table[:2]
 for call_rows in call_table:
     call_cols_list = call_rows.find_all('td')
     strike_list.append(call_cols_list[0].find('a').string.encode('ascii','ignore'))
     symbol_list.append(call_cols_list[1].find('a').string.encode('ascii','ignore'))
     last_list.append(call_cols_list[2].string.encode('ascii','ignore'))
     chg_list.append(call_cols_list[3].find('b').string.encode('ascii','ignore'))
     bid_list.append(call_cols_list[4].string.encode('ascii','ignore'))
     ask_list.append(call_cols_list[5].string.encode('ascii','ignore'))
     vol_list.append(call_cols_list[6].string.encode('ascii','ignore'))
     open_list.append(call_cols_list[7].string.encode('ascii','ignore'))

 for put_rows in put_table:
     put_cols_list = put_rows.find_all('td')
     strike_list.append(put_cols_list[0].find('a').string.encode('ascii','ignore'))
     symbol_list.append(put_cols_list[1].find('a').string.encode('ascii','ignore'))
     last_list.append(put_cols_list[2].string.encode('ascii','ignore'))
     chg_list.append(put_cols_list[3].find('b').string.encode('ascii','ignore'))
     bid_list.append(put_cols_list[4].string.encode('ascii','ignore'))
     ask_list.append(put_cols_list[5].string.encode('ascii','ignore'))
     vol_list.append(put_cols_list[6].string.encode('ascii','ignore'))
     open_list.append(put_cols_list[7].string.encode('ascii','ignore'))

 for symbols in symbol_list:
    pattern_match_symbols = re.search("[a-zA-Z]+[0-9]+[a-zA-Z]",symbols)
    group1 =  pattern_match_symbols.group()
    symbol_type.append(group1[-1])
    group1 = group1[:-1]
    sticker_symbol.append(group1[:-6])
    Date.append(group1[-6:])
 options_dict = {}
 def json_list(ask_value,bid_list,chg_list,Date,last_list,open_list,strike_list,sticker_symbol,symbol_type,vol_list):
    options_list = []
    options_dict_new = {}
    options_dict['Ask'] = ask_value
    options_dict['Bid'] = bid_list
    options_dict['Change'] = " "+chg_list
    options_dict['Date'] = Date
    options_dict['Last'] = last_list
    options_dict['Open'] = open_list
    options_dict['Strike'] = strike_list
    options_dict['Symbol'] = sticker_symbol
    options_dict['Type'] = symbol_type
    options_dict['Vol'] = vol_list
    options_list.append(options_dict)
    return json.dumps(options_dict, sort_keys=True)

 json_dump_list = []
 json_dump_options_list = []
 json2_data = []
 attr_index = 0
 for attribute_values in ask_list:
     json_dump_list.append(json_list(ask_list[attr_index],bid_list[attr_index],chg_list[attr_index],Date[attr_index],last_list[attr_index],
     open_list[attr_index],strike_list[attr_index],sticker_symbol[attr_index],symbol_type[attr_index],vol_list[attr_index]))
     attr_index += 1
 for list_items in json_dump_list:
    json_dump_options_list.append(ast.literal_eval(list_items))
 sorted_options_list = sorted(json_dump_options_list, key=lambda k: int(k["Open"].replace(",", "")), reverse=True)
 Output_array = {}
 Output_array["currPrice"] = float(currentprice)
 Output_array["dateUrls"] = return_urls
 Output_array["optionQuotes"] = sorted_options_list
 jsonQuoteData = json.dumps(Output_array, sort_keys=True)
 return jsonQuoteData








