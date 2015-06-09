##############################################################
### A MODULE MAVEN WITH MANY UTILITY CLASS FOR MANY PURPOSE###
##############################################################

Class utility for the third library:
- Many Useful Class for Pay with the JDK version 7
- JDOM2
- Hibernate 4 (in progress)
- Jena 2

(For the future try to put some JDK version 8)

Any Help,suggestion or improvement for this utility is welcome!!

P.S. I'm need help for create very useful method for update xml file without create/delete already exists file
with only jdk use wihout third library.

For the rest do what you want with these file.

[![Release](https://img.shields.io/github/release/4535992/utility.svg?label=maven)](https://jitpack.io/4535992/utility)

You can the dependency to this github repository With jitpack (https://jitpack.io/):

<!-- Put the Maven coordinates in your HTML: -->
 <pre class="prettyprint">&lt;dependency&gt;
  &lt;groupId&gt;com.github.4535992&lt;/groupId&gt;
  &lt;artifactId&gt;utility&lt;/artifactId&gt;
  &lt;version&gt;<span id="latest_release">1.0</span>&lt;/version&gt;
&lt;/dependency&gt;  </pre>

<!-- Add this script to update "latest_release" span to latest version -->
<script>
      var user = '4535992'; // Replace with your user/repo
      var repo = 'utility'

      var xmlhttp = new XMLHttpRequest();
      xmlhttp.onreadystatechange = function() {
          if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
              var myArr = JSON.parse(xmlhttp.responseText);
              populateRelease(myArr);
          }
      }
      xmlhttp.open("GET", "https://api.github.com/repos/" user + "/" + repo + "/releases", true);
      xmlhttp.send();

      function populateRelease(arr) {
          var release = arr[0].tag_name;
          document.getElementById("latest_release").innerHTML = release;
      }
</script>
