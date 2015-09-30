/*
 * The MIT License
 *
 * Copyright (c) 2010-2015 tap4j team (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tap4j.parser.issueGitHub37;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerImpl;
import org.tap4j.model.TestSet;
import org.tap4j.parser.Tap13Parser;

/**
 * Makes sure that a SKIP directive is not taken as a comment.
 *
 * @author Bruno P. Kinoshita
 * @since 4.1.1
 */
public class TestSkipDirective {

	@Test
	public void testSkipDirectivePresent() {
		TapConsumer consumer = new TapConsumerImpl(new Tap13Parser("ISO-8859-1", false,  false));
		TestSet ts = consumer.load("#cat /var/lib/jenkins/jobs/gh-mellanox-v1.8-PR/builds/137/tap-master-files/cov_stat.tap\n" + 
				"not ok 1 #SKIP\n" + 
				"ok 2 - coverity found no issues for oshmem\n" + 
				"ok 3 - coverity found no issues for yalla\n" + 
				"ok 4 - coverity found no issues for mxm\n" + 
				"ok 5 - coverity found no issues for fca\n" + 
				"ok 6 - coverity found no issues for hcoll");
		assertTrue(ts.getTestResults().get(0).getDirective() != null);
	}
	
}
