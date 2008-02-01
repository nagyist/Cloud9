/*
 * Cloud9: A MapReduce Library for Hadoop
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.umd.cloud9.demo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;

import edu.umd.cloud9.tuple.ListWritable;
import edu.umd.cloud9.tuple.Schema;
import edu.umd.cloud9.tuple.Tuple;
import edu.umd.cloud9.util.LocalTupleRecordWriter;

/**
 * <p>
 * Demo that packs the sample collection into records using the tuple library,
 * illustrating the use of the {@link edu.umd.cloud9.tuple.Tuple} and
 * {@link edu.umd.cloud9.tuple.ListWritable} classes. The records are stored in
 * a local SequenceFile; this file can then be transfered over to HDFS to serve
 * as the starting point for a MapReduce operation.
 * </p>
 * 
 * <p>
 * Each record is a tuple with two fields:
 * </p>
 * 
 * <ul>
 * 
 * <li>the first field of the tuple is an Integer with the field name "length";
 * its value is the length of the record in number of characters.</li>
 * 
 * <li>the second field of the tuple is a ListWritable<Text> with the field
 * name "tokens"; its value is a list of tokens that comprise the text of the
 * record.</li>
 * 
 * </ul>
 * 
 * @see DemoPackRecords
 * @see DemoReadPackedRecords2
 */
public class DemoPackRecords2 {
	private DemoPackRecords2() {
	}

	// define the tuple schema for the input record
	private static final Schema RECORD_SCHEMA = new Schema();
	static {
		RECORD_SCHEMA.addField("length", Integer.class);
		RECORD_SCHEMA.addField("tokens", ListWritable.class, "");
	}

	// instantiate a single tuple
	private static Tuple tuple = RECORD_SCHEMA.instantiate();

	/**
	 * Runs the demo.
	 */
	public static void main(String[] args) throws IOException {
		String infile = "../umd-hadoop-dist/sample-input/bible+shakes.nopunc";
		String outfile = "../umd-hadoop-dist/sample-input/bible+shakes.nopunc.packed2";

		// create LocalTupleRecordWriter to write tuples to a local SequenceFile
		LocalTupleRecordWriter writer = new LocalTupleRecordWriter(outfile);

		// read in raw text records, line separated
		BufferedReader data = new BufferedReader(new InputStreamReader(
				new FileInputStream(infile)));

		String line;
		while ((line = data.readLine()) != null) {
			ListWritable<Text> tokens = new ListWritable<Text>();
			StringTokenizer itr = new StringTokenizer(line);
			while (itr.hasMoreTokens()) {
				tokens.add(new Text(itr.nextToken()));
			}

			// write the record
			tuple.set("length", line.length());
			tuple.set("tokens", tokens);
			writer.add(tuple);
		}

		data.close();
		writer.close();

		System.out.println("Wrote " + writer.getRecordCount() + " records.");
	}
}