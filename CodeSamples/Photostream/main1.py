#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from google.appengine.api import users
from google.appengine.ext import ndb
from google.appengine.api import images
from google.appengine.api import mail
from datetime import datetime
import webapp2
import urllib
import json
import cgi
import re

loginhtml = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Login Page</title>
</head>

<body>
<h1>Welcome to Connexus!</h1>
<h2>Share the World!</h2>
<form method="post">
  <input type="text" name="username" placeholder="userID" required/>
  <br> <br>
  <input type="password" name="password" placeholder="Password" required/>
  <br><br>
  <input type="submit" value="Login"/>
</form>
</body>
</html>
"""

html = """
<!doctype html>
<html>
<head>
<meta charset="utf-8"/>
<title> Create Stream</title>
<style>
a:link {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:visited {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:hover {
    color: red;
    background-color: transparent;
    text-decoration: none;
}
a:active {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
a.current {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}

</style>
</head>
<body> <h2>Connex.us</h2> 
<a href="/Manage">Manage</a> &nbsp | &nbsp
<a href="/Create" class = "current">Create</a>  &nbsp | &nbsp
<a href="/View">View</a>  &nbsp | &nbsp
<a href="/Search">Search</a>  &nbsp | &nbsp
<a href="/Trending">Trending</a>  &nbsp | &nbsp
<a href="social.html">Social</a>
<h4>Streams I own</h4>
<form method="post" enctype="multipart/form-data">
<table>
    <tr style="height:20px;"></tr>
    <tr>
        <td><input type = "text" name="streamNameId" style="height: 20px; width:180px" /></td>
        <td style="row-span:4"></td>
        <td><input type = "text" name="streamTagId" style="height: 20px; width:180px" /></td>
    </tr>
    <tr>
        <td><label for="streamNameId">Name your stream</label></td>
        <td style="row-span:4"></td>
        <td><label for="streamTagId">Tag your stream</label></td>
    </tr>
    <tr style="height:40px;"></tr>
    <tr>
        <td>
            <table>
                <tr><td><input type="text" name="streamSubscriberId" placeholder="Email IDs of subscribers" style="height: 60px; width:180px"></td></tr>
                <tr style="height:15px;"></tr>
                <tr><td><input type="text" name="optionalMessageId" placeholder="Optional message for invite" style="height: 60px; width:180px"></td></tr>
                <tr><td><label for="streamSubscriberId">Add Subscribers</label></td></tr>
            </table>
        </td>
        <td style="width:40px"></td>
        <td>
            <table>
                 <tr><td><input type="file" name="urlId" id="urlID"style="height:25px; width:180px"></td></tr>
                 <tr><td><label for="urlId">URL to cover image</label></td></tr>
            </table>

        </td>
    </tr>
    <tr style="height:20px;"></tr>
    <tr><td><input type="submit" id="btnCreateStream" value="Create Stream" onclick="return myFunction()"></td></tr>
</table>

</form>
<script>
function myFunction(){
     if (document.getElementById("urlID").files.length == 0 ){
         alert("Error! Choose cover image to upload..");
         return false;}
         }
         </script>
</body>
</html>
"""

managehtml = """<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Management Page</title>
<style>
a:link {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:visited {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:hover {
    color: red;
    background-color: transparent;
    text-decoration: none;
}
a:active {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
a.current {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
table {
  border-collapse: collapse;
}

td, th {
  border: 1px solid #999;
  padding: 0.5rem;
  text-align: left;
}
</style>
    <script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
    <script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
        $("#del_stream").click(function(){
        var checekedlist = $('input[name="del_checked"]:checked').map(function(){
            return $(this).val();
        }).get();
       if(checekedlist.length == 0)
       {
        alert("No checkboxes selected");
       }
       else
       {
         $.ajax({
                type: "POST",
                url: "/delete_checked",
                data: JSON.stringify(checekedlist),
                contentType: "application/json",
                dataType: 'json',
            success: function(){
            alert("Delete Successful");
            $('input:checkbox').removeAttr('checked');
            window.location.reload();
         }
        });
        }
      });
      $("#unsub_stream").click(function(){
        var sublist = $('input[name="unsubscribe_checked"]:checked').map(function(){
            return $(this).val();
        }).get();
       if(sublist.length == 0)
       {
        alert("No checkboxes selected");
       }
       else
       {
         $.ajax({
                type: "POST",
                url: "/delete_unsub",
                data: JSON.stringify(sublist),
                contentType: "application/json",
                dataType: 'json',
            success: function(){
            alert("Delete Successful");
            $('input:checkbox').removeAttr('checked');
            window.location.reload();
         }
        });
        }
      });
    });
    </script>
</head>
<body>
<h1> Connex.us</h1>
<a href="/Manage" class = "current">Manage</a> &nbsp | &nbsp
<a href="/Create">Create</a>  &nbsp | &nbsp
<a href="/View">View</a>  &nbsp | &nbsp
<a href="/Search">Search</a>  &nbsp | &nbsp
<a href="/Trending">Trending</a>  &nbsp | &nbsp
<a href="social.html">Social</a>
<h4>Streams I own</h4>
</body></html>"""

errorhtml = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Error Page</title>
<style>
a:link {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:visited {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:hover {
    color: red;
    background-color: transparent;
    text-decoration: none;
}
a:active {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
a.current {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
</style>
</head>

<body>
<h1> Connex.us</h1>
<a href="/Manage">Manage</a> &nbsp | &nbsp
<a href="/Create">Create</a>  &nbsp | &nbsp
<a href="/View">View</a>  &nbsp | &nbsp
<a href="/Search">Search</a>  &nbsp | &nbsp
<a href="/Trending">Trending</a>  &nbsp | &nbsp
<a href="social.html">Social</a>
<br><br><br>
<img src="error.jpg" align="left" alt=""/><p>Error:You tried to create a new stream whose name is the same as an existing stream or you have not specified a stream name,operation did not complete</p>
</body>
</html> """

viewstreamhtml = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus View Stream Page</title>
<style>
a:link {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:visited {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:hover {
    color: red;
    background-color: transparent;
    text-decoration: none;
}
a:active {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
a.current {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}

</style>
</head>

<h1> Connex.us</h1>
<a href="/Manage">Manage</a> &nbsp | &nbsp
<a href="/Create">Create</a>  &nbsp | &nbsp
<a href="/View" class="current">View</a>  &nbsp | &nbsp
<a href="/Search">Search</a>  &nbsp | &nbsp
<a href="/Trending">Trending</a>  &nbsp | &nbsp
<a href="social.html">Social</a><br><br><br>
"""

viewall = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus View Stream Page</title>
<style>
a:link {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:visited {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:hover {
    color: red;
    background-color: transparent;
    text-decoration: none;
}
a:active {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
a.current {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}

</style>
</head>
<body>
<h1> Connex.us</h1>
<a href="/Manage">Manage</a> &nbsp | &nbsp
<a href="/Create">Create</a>  &nbsp | &nbsp
<a href="/View" class="current">View</a>  &nbsp | &nbsp
<a href="/Search">Search</a>  &nbsp | &nbsp
<a href="/Trending">Trending</a>  &nbsp | &nbsp
<a href="social.html">Social</a>
<h2>View All Streams</h2>
</body>
</html>
"""

searchhtml = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Search Page</title>
<style>
a:link {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:visited {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:hover {
    color: red;
    background-color: transparent;
    text-decoration: none;
}
a:active {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
a.current {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
</style>
</head>
<body>
<h2>Connex.us</h2>
<a href="/Manage">Manage</a> &nbsp | &nbsp
<a href="/Create">Create</a>  &nbsp | &nbsp
<a href="/View">View</a>  &nbsp | &nbsp
<a href="/Search" class="current">Search</a>  &nbsp | &nbsp
<a href="/Trending">Trending</a>  &nbsp | &nbsp
<a href="social.html">Social</a><br><br><br>
</body></html>
"""

trendhtml="""
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Connexus Trending Page</title>
<style>
a:link {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:visited {
    color: blue;
    background-color: transparent;
    text-decoration: none;
}
a:hover {
    color: red;
    background-color: transparent;
    text-decoration: none;
}
a:active {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
a.current {
    color: purple;
    background-color: transparent;
    text-decoration: none;
}
.cb-row {margin: 10px;clear:both;overflow:hidden;}
.cb-row label {float:right;}
.cb-row input {float:right;}

</style>
</head>

<body>
<h1> Connex.us</h1>
<a href="/Manage">Manage</a> &nbsp | &nbsp
<a href="/Create">Create</a>  &nbsp | &nbsp
<a href="/View">View</a>  &nbsp | &nbsp
<a href="/Search">Search</a>  &nbsp | &nbsp
<a href="/Trending" class="current">Trending</a>  &nbsp | &nbsp
<a href="social.html">Social</a>
<h2>Top 3 Trending Streams</h2>
<div class="update_rate">
	<form method="post" id="email_freq_set" name="email_freq_set"><table>
	
	<tr><td><input type="radio" name="email_freq" value="0" >No reports</td></tr>
	<tr><td><input type="radio" name="email_freq" value="1">Every 5 minutes</td></tr>
	<tr><td><input type="radio" name="email_freq" value="2">Every 1 hour</td></tr>
	<tr><td><input type="radio" name="email_freq" value="3">Every day</td></tr>
	<tr><td>Email trending report</td></tr>
	<tr><td><input class="button" type="submit" value="Update rate"></td></tr>
	</table></form>
	</div>
</body>
</html>
"""
class Image(ndb.Model):
     img = ndb.BlobProperty()
     comments = ndb.StringProperty()
     date = ndb.DateTimeProperty(auto_now_add=True)
class Stream(ndb.Model):
     name = ndb.StringProperty()
     tag = ndb.StringProperty()
     subscribers = ndb.StringProperty(repeated=True)
     invite = ndb.StringProperty()
     cover = ndb.BlobProperty()
     userID = ndb.StringProperty()
     views = ndb.IntegerProperty(default=0)
     viewtimelist = ndb.GenericProperty(repeated=True)
     numpics = ndb.IntegerProperty(default=0)
     pics = ndb.StructuredProperty(Image, repeated=True)
     datetime = ndb.DateTimeProperty(auto_now_add=True)
     keys = ndb.KeyProperty(repeated=True)
     
class LoginPage(webapp2.RequestHandler):

    def get(self):
        self.response.write(loginhtml)

    def post(self):
        user = users.get_current_user()

        if user:
            self.redirect('/Manage')
        else:
            self.redirect(users.create_login_url(self.request.uri))

class MainPage(webapp2.RequestHandler):

    def get(self):
        user = users.get_current_user()

        if user:
            pass
        else:
            self.redirect(users.create_login_url(self.request.uri))

        self.response.write(html)

    def post(self):
        user = users.get_current_user()
        stream = Stream()
        stream.name = self.request.get("streamNameId")
        stream.tag = self.request.get("streamTagId")
        subscribers = self.request.get("streamSubscriberId").split(',')
        for each_subscriber in subscribers:
            (stream.subscribers).append(each_subscriber)
        stream.invite = self.request.get("optionalMessageId")
        cover = self.request.get('urlId')
        # cover = images.resize(cover, 150, 150)
        stream.cover = cover
        stream.userID = user.user_id()
        existing_values = Stream.query(Stream.userID != "").fetch()
        flag=0
        for value in existing_values:
            if((not stream.name) or ((value.name).upper() == (stream.name).upper())):
                flag=1
                break

        if(flag == 1):
            self.redirect('/Error')
        else:
            stream_key = stream.put()
            self.redirect('/Manage?')

class ManagePage(webapp2.RequestHandler):
    def get(self):
        
        self.response.write(managehtml)
        q = Stream.query(Stream.userID != "").fetch()
        self.response.write('<html><body><table><thead><th>Name</th><th>Last New Picture</th><th>Number of Pictures</th><th>Delete</th></thead>')
        for stream in q:
            self.response.write('<tr><td><a href="/view?s=%s">%s</a></td><td>%s</td><td>%s</td><td><input type="checkbox" value=%s name="del_checked"/></td></tr>' % (stream.name,stream.name,stream.datetime.strftime('%m/%d/%Y'),stream.numpics,stream.name))
        self.response.write('</table><br>')
        self.response.write('<input type="submit" value="Delete Checked" id="del_stream"/>')
        self.response.write('<h4>Streams I subscribe to</h4><table><thead><th>Name</th><th>Last New Picture</th><th>Number of Pictures</th><th>Views</th><th>Unsubscribe</th></thead>')
        user_to_remove = users.get_current_user()
        for subscribe_list in q:
            for emails in subscribe_list.subscribers:
                if(user_to_remove.email() == emails):
                    self.response.write('<tr><td><a href="/view?s=%s">%s</a></td><td>%s</td><td>%s</td><td>%s</td><td><input type="checkbox" value=%s name="unsubscribe_checked"/></td></tr>' % (subscribe_list.name,subscribe_list.name,subscribe_list.datetime.strftime('%m/%d/%Y'),subscribe_list.numpics,subscribe_list.views,subscribe_list.name))
        self.response.write('</table><br>')
        self.response.write('<input type="submit" value="Unsubscribe Checked Streams" id="unsub_stream"/>')
        self.response.write('</body></html>')
        
class Deletesublist(webapp2.RequestHandler):
    def post(self):
        sdata = json.loads(cgi.escape(self.request.body))
        for slist in sdata:
            stream_to_subdelete =  Stream.query(Stream.name == slist).get()
            stream_to_subdelete.subscribers = [x.encode('utf-8') for x in stream_to_subdelete.subscribers]
            user = users.get_current_user()
            for email_entry in stream_to_subdelete.subscribers:
                if(email_entry == user.email()):
                    stream_to_subdelete.subscribers.remove(email_entry)
                    stream_to_subdelete.subscribers = [x.decode('utf-8') for x in stream_to_subdelete.subscribers]
                    stream_to_subdelete.put()
                    

class DeleteStream(webapp2.RequestHandler):
    def post(self):
        jdata = json.loads(cgi.escape(self.request.body))
        for checked_field in jdata:
            user_to_delete = Stream.query(Stream.name == checked_field).get()
            user_to_delete.key.delete()
            
class ErrorPage(webapp2.RequestHandler):
    def get(self):
        self.response.write(errorhtml)

class SearchStream(webapp2.RequestHandler):
    def get(self):
        self.response.write(searchhtml)
        self.response.write("""
        <form method="post">
<div style = "height:20px";></div>
<input type="text" name="searchStreamId" placeholder="Search Streams" style="width:160px">
<br> <br>
<input type="submit" id="btnSearchStream" value="Search">
</form>
        """)

    def post(self):
        search_string = cgi.escape(self.request.get('searchStreamId'))
        s = Stream.query(Stream.userID != "").fetch()
        self.response.write(searchhtml)
        count = 0
        for st in s:
            if (search_string != "") and (search_string.lower() in st.name.lower()):
               htmlimg = '<p style="float: left; font-size: 9pt; text-align: center; width: 200px; margin-right: 30px; margin-bottom: 0.5em;"><a href = "/view?s=%s"><img src="/image?key=%s" style="width: 100px"><br>%s</a></p>' % (st.name, st.key.urlsafe(), st.name)
               self.response.out.write(htmlimg)
               count = count + 1   
        if count == 0:
            self.response.out.write("No matching Stream found!")        
            #if re.search(search_string,stream_data.name,re.IGNORECASE):
                #self.response.out.write(stream_data.name)
           
class ViewallStreams(webapp2.RequestHandler):

    def get(self):
        self.response.write(viewall) 
        stream = Stream.query().order(Stream.datetime).fetch()
        for st in stream:
           #htmlimg = '<img src="/image?key=%s" />' % st.key.urlsafe()
           #htmlimg = '<img src="/image?key=%s" style="float: left; width: 200px; margin-right: 30px; margin-bottom: 0.5em;">' % st.key.urlsafe()
           htmlimg = '<p style="float: left; font-size: 9pt; text-align: center; width: 200px; margin-right: 30px; margin-bottom: 0.5em;"><a href = "/view?s=%s"><img src="/image?key=%s" style="width: 100px"><br>%s</a></p>' % (st.name, st.key.urlsafe(), st.name)
           self.response.out.write(htmlimg)   
                      
    
class ServeImage(webapp2.RequestHandler):
     def get(self):
        stkey = ndb.Key(urlsafe=self.request.get('key'))
        image = stkey.get()
        if image.cover:
             self.response.headers['Content-Type'] = 'image/png'
             self.response.out.write(image.cover)
        else:
            self.abort(404)
            
class Serveimage(webapp2.RequestHandler):
     def get(self):
        stkey = ndb.Key(urlsafe=self.request.get('key'))
        stream = stkey.get()
        indx = self.request.get('ind')
        indx = int(indx)
        k = stream.keys[indx]
        image_key = ndb.Key(k.kind(),k.id())
        #self.response.out.write(image_key)
        image = image_key.get()
        if image.img:
             self.response.headers['Content-Type'] = 'image/png'
             self.response.out.write(image.img)
        else:
            self.abort(404)            
            
class ViewStream(webapp2.RequestHandler):

    def get(self):
        self.response.write(viewstreamhtml) 
        st = self.request.get('s')
        stream = Stream.query(Stream.name == st).get()
        if stream is not None:
           stream.views = stream.views + 1
           stream.viewtimelist.append(datetime.now())
           viewlist = stream.viewtimelist
           for time in viewlist:
               if (datetime.now() - time).seconds >= 3600:
                   viewlist.remove(time)
                   stream.views = stream.views - 1
               else:
                   break
           stream.viewtimelist = viewlist
           n = stream.name
           stream.put()
        else:
           st = self.request.get('ss')
           stream = Stream.query(Stream.name == st).get()
           n = stream.name
           
           
        for pic in stream.pics:
            htmlimg = '<img src="/imag?key=%s&ind=%s" style="float: left; width: 200px; margin-right: 30px; margin-bottom: 0.5em;">' % (stream.key.urlsafe(), stream.pics.index(pic))
            self.response.out.write(htmlimg)
            
        l = len(stream.pics)    
        self.response.write("""
        <br><br>
        <form method="post" enctype="multipart/form-data">
        <input type="hidden" name="st" value = "%s">
        <input type="hidden" name="ind" value = "%s">
        <br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
<div style = "height:20px";></div>
<div id = "div_addImage" style = "height:80px; width:350px; border-style:solid; border-width:1px; border-color: black; padding: 5px">
    <table>
        <tr>
            <td>
                <table>
                <tr><td><input type="file" name="fileNameId"  id = "fileID" style="height:20px; width:100px"></td></tr>
                <tr style="height:15px;"></tr>
                <tr><td><input type="submit" name="action" value="Upload file" onclick="return myFunction()"></td></tr>
                </table>
            </td>
            <td style="width:40px"></td>
            <td>
                <table>
                    <tr><td><input type="text" name="commentsId" placeholder="Comments" style="height:25px; width:180px;"></td></tr>
                    <tr><td style="height:30px;"><label>Add an Image</label></td></tr>
                </table>
            </td>
        </tr>
    </table>
</div>
<div style = "height:20px";></div>
<div style = "height:80px;">
    <input type="submit" name="action" value="Subscribe"></td></tr>
</div>
<script>
function myFunction(){
     if (document.getElementById("fileID").files.length == 0 ){
         alert("Error! Choose image to upload..");
         return false;}
         }
         </script>
</form>
""" % (n, l))
        

    def post(self):
        act = self.request.get("action")
        if act == "Upload file":
           stream = Stream.query(Stream.name == self.request.get('st')).get()
           ln = self.request.get('ind')
           image = self.request.get('fileNameId')
           comment = self.request.get('commentsId')
           newimage = Image(img=image,comments=comment)
           stream.pics.append(newimage)
           stream.numpics = stream.numpics + 1
           img_key = newimage.put()
           stream.keys.append(img_key)
           stream.keys = list(reversed(stream.keys))
           stream.pics = list(reversed(stream.pics))
           stream.put()
           self.redirect('/view?' + urllib.urlencode(
              {'ss': stream.name}))
        else:
           stream = Stream.query(Stream.name == self.request.get('st')).get()
           user = users.get_current_user()
           if user:
              stream.subs = [x.encode('utf-8') for x in stream.subscribers]
              stream.subs.append(user.email())
              stream.subscribers = [x.decode('utf-8') for x in stream.subs]
              stream.put()
           else:
              self.redirect(users.create_login_url(self.request.uri))
           self.redirect('/view?' + urllib.urlencode(
              {'ss': stream.name}))       
            
class Trending(webapp2.RequestHandler):
    def get(self):
        self.response.write(trendhtml)
    def post(self):    
        self.response.write(trendhtml)
        frequency = self.request.get("email_freq")
        if frequency == "1":
            self.redirect('/fivemins?')
        elif frequency == "2":
            self.redirect('/hourly?')
        elif frequency == "3":
            self.redirect('/daily?')
        else:
            pass        
        list = topthree()
        for l in list:
            nm = l["id"]
            st = Stream.query(Stream.name == nm).get()
            htmlimg = '<p style="float: left; font-size: 9pt; text-align: center; width: 200px; margin-right: 30px; margin-bottom: 0.5em;"><img src="/image?key=%s" style="width: 100px"><br>%s<br>%d views</p>' % (st.key.urlsafe(), st.name, st.views)
            self.response.out.write(htmlimg)
            
def topthree():        
        stream = Stream.query().fetch()
        liststr = []
        for st in stream:
            dict = {"id": st.name}
            viewlist = st.viewtimelist
            for time in viewlist:
               if (datetime.now() - time).seconds >= 3600:
                   viewlist.remove(time)
               else:
                   break
            st.viewtimelist = viewlist
            st.put()
            dict["length"] = len(viewlist)
            liststr.append(dict) 
        list = sorted(liststr, key=lambda x:x['length'], reverse=True)
        return list[0:3]        
        
class Fivemins(webapp2.RequestHandler):
    def get(self):
        list = topthree()
        count = len(list)
        message = mail.EmailMessage(sender="sushmahampapur@gmail.com",
                                subject="Trending streams")
        message.to = "nima.dini@utexas.edu"
        if len == 0:
            message.body = "No streams found"
        elif len == 1:
            message.body = """
            Top 3 Trending Streams:
            Stream1: %s views:%d
            Stream2: None
            Stream3: None
            """ %((list[0])["id"],(list[0])["length"])
        elif len == 2:
            message.body = """
            Top 3 Trending Streams:
            Stream1: %s views:%d
            Stream2: %s views:%d
            Stream3: None
            """ %((list[0])["id"],(list[0])["length"],
	              (list[1])["id"],(list[1])["length"],)
        elif len == 3:
            message.body = """
            Top 3 Trending Streams:
            Stream1: %s views:%d
            Stream2: %s views:%d
            Stream3: %s views:%d
            """ %((list[0])["id"],(list[0])["length"],
	              (list[1])["id"],(list[1])["length"],
	              (list[2])["id"],(list[2])["length"])                  
        message.send()
            
class Hourly(webapp2.RequestHandler):
    def get(self):
        list = topthree()
        count = len(list)
        message = mail.EmailMessage(sender="sushmahampapur@gmail.com",
                                subject="Trending streams")
        message.to = "nima.dini@utexas.edu"
        if len == 0:
            message.body = "No streams found"
        elif len == 1:
            message.body = """
            Top 3 Trending Streams:
            Stream1: %s views:%d
            Stream2: None
            Stream3: None
            """ %((list[0])["id"],(list[0])["length"])
        elif len == 2:
            message.body = """
            Top 3 Trending Streams:
            Stream1: %s views:%d
            Stream2: %s views:%d
            Stream3: None
            """ %((list[0])["id"],(list[0])["length"],
	              (list[1])["id"],(list[1])["length"],)
        elif len == 3:
            message.body = """
            Top 3 Trending Streams:
            Stream1: %s views:%d
            Stream2: %s views:%d
            Stream3: %s views:%d
            """ %((list[0])["id"],(list[0])["length"],
	              (list[1])["id"],(list[1])["length"],
	              (list[2])["id"],(list[2])["length"])                  
        message.send()

class Daily(webapp2.RequestHandler):
    def get(self):
        list = topthree()
        count = len(list)
        message = mail.EmailMessage(sender="sushmahampapur@gmail.com",
                                subject="Trending streams")
        message.to = "nima.dini@utexas.edu"
        if len == 0:
            message.body = "No streams found"
        elif len == 1:
            message.body = """
            Top 3 Trending Streams:
            Stream1: %s views:%d
            Stream2: None
            Stream3: None
            """ %((list[0])["id"],(list[0])["length"])
        elif len == 2:
            message.body = """
            Top 3 Trending Streams:
            Stream1: %s views:%d
            Stream2: %s views:%d
            Stream3: None
            """ %((list[0])["id"],(list[0])["length"],
	              (list[1])["id"],(list[1])["length"],)
        elif len == 3:
            message.body = """
            Top 3 Trending Streams:
            Stream1: %s views:%d
            Stream2: %s views:%d
            Stream3: %s views:%d
            """ %((list[0])["id"],(list[0])["length"],
	              (list[1])["id"],(list[1])["length"],
	              (list[2])["id"],(list[2])["length"])                  
        message.send()            
            
app = webapp2.WSGIApplication([
    ('/', LoginPage),
    ('/Create', MainPage),
    ('/Manage', ManagePage),
    ('/delete_checked', DeleteStream),
    ('/delete_unsub', Deletesublist),
    ('/Search', SearchStream),
    ('/Error', ErrorPage),
    ('/view', ViewStream),
    ('/View', ViewallStreams),
    ('/image', ServeImage),
    ('/imag', Serveimage),
    ('/Trending', Trending),
    ('/fivemins', Fivemins),
    ('/hourly', Hourly),
    ('/daily', Daily)
], debug=True)
