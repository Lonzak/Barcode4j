<#--
  #%L
  License Maven Plugin
  %%
  Copyright (C) 2012 Codehaus, Tony Chemit
  %%
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Lesser Public License for more details.

  You should have received a copy of the GNU General Lesser Public
  License along with this program.  If not, see
  <http://www.gnu.org/licenses/lgpl-3.0.html>.
  #L%
  -->

<#--
	description:
	Freemarker template file used to calculate and format the list of 3rd party dependencies of the project (into an html file) 
	Output is in html table format, columns: project/dependency name (or artifactId) | License | Link to project or license
-->


<#-- To render the third-party file.
 Available context :

 - dependencyMap a collection of Map.Entry with
   key are dependencies (as a MavenProject) (from the maven project)
   values are licenses of each dependency (array of string)

 - licenseMap a collection of Map.Entry with
   key are licenses of each dependency (array of string)
   values are all dependencies using this license
-->
<#function artifactFormat p licenses>
	<#assign url = "" />

	<#assign licensesStr = "" />
    <#list licenses as license>
        <#assign licensesStr = licensesStr + license + " " />
    </#list>
	
	<#if licensesStr?index_of('|') &gt; -1>
		<#assign pos = licensesStr?index_of('|') />
		<#assign url = "<a href=\"" + licensesStr[pos+1..]?trim + "\">" + licensesStr[pos+1..]?trim + "</a>" />
		<#assign licensesStr = licensesStr[0..pos-1]>
	<#elseif p.url??>
		<#assign url = "<a href=\"" + p.url + "\">" + p.url + "</a>" />
	<#else>
		<#assign url = "-" />
	</#if>

    <#if p.name?index_of('Unnamed') &gt; -1>
        <#return "<td>" + p.artifactId + "</td><td>" + licensesStr + "</td><td>" + url + "</td>">
    <#else>
        <#return "<td>" + p.name + "</td><td>" + licensesStr + "</td><td>" + url + "</td>">
    </#if>
</#function>

<#if dependencyMap?size == 0>
No dependencies.
<#else>
<html>
<h1>Third party dependencies:</h1>
<table border="1">
<tr><th>Project</th><th>License</th><th>Link</th></tr>
<!-- HEADER END (DO NOT MODIFY) -->
    <#list dependencyMap as e>
        <#assign project = e.getKey()/>
        <#assign licenses = e.getValue()/>
<tr>${artifactFormat(project, licenses)}</tr>
    </#list>
<!-- FOOTER START (DO NOT MODIFY)-->
</table>
</html>
</#if>
