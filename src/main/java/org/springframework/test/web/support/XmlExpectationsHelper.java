/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.web.support;

import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.springframework.test.web.AssertionErrors;
import org.springframework.test.web.server.result.MockMvcResultMatchers;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * A helper class for assertions on XML content.
 *
 * @author Rossen Stoyanchev
 */
public class XmlExpectationsHelper {


	/**
	 * Parse the content as {@link Node} and apply a {@link Matcher}.
	 * @see org.hamcrest.Matchers#hasXPath
	 */
	public void assertNode(String content, Matcher<? super Node> matcher) throws Exception {
		Document document = parseXmlString(content);
		MatcherAssert.assertThat("Body content", document, matcher);
	}

	private Document parseXmlString(String xml) throws Exception  {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		InputSource inputSource = new InputSource(new StringReader(xml));
		Document document = documentBuilder.parse(inputSource);
		return document;
	}

	/**
	 * Parse the content as {@link DOMSource} and apply a {@link Matcher}.
	 * @see <a href="http://code.google.com/p/xml-matchers/">xml-matchers</a>
	 */
	public void assertSource(String content, Matcher<? super Source> matcher) throws Exception {
		Document document = parseXmlString(content);
		MatcherAssert.assertThat("Body content", new DOMSource(document), matcher);
	}

	/**
	 * Parse the expected and actual content strings as XML and assert that the
	 * two are "similar" -- i.e. they contain the same elements and attributes
	 * regardless of order.
	 *
	 * <p>Use of this method assumes the
	 * <a href="http://xmlunit.sourceforge.net/">XMLUnit<a/> library is available.
	 *
	 * @param expected the expected XML content
	 * @param actual the actual XML content
	 *
	 * @see MockMvcResultMatchers#xpath(String, Object...)
	 * @see MockMvcResultMatchers#xpath(String, Map, Object...)
	 */
	public void assertXmlEqual(String expected, String actual) throws Exception {
		Document control = XMLUnit.buildControlDocument(expected);
		Document test = XMLUnit.buildTestDocument(actual);
		Diff diff = new Diff(control, test);
		if (!diff.similar()) {
			AssertionErrors.fail("Body content " + diff.toString());
		}
	}

}
