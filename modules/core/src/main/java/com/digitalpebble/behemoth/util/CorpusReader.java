/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digitalpebble.behemoth.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.digitalpebble.behemoth.BehemothConfiguration;
import com.digitalpebble.behemoth.BehemothDocument;

/**
 * Utility class used to read the content of a Behemoth SequenceFile.
 **/
public class CorpusReader extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(BehemothConfiguration.create(),
                new CorpusReader(), args);
        System.exit(res);
    }

    public int run(String[] args) throws Exception {

        Path input = new Path(args[0]);

        boolean showBinaryContent = false;
        if (args.length > 1 && "-showBinaryContent".equalsIgnoreCase(args[1]))
            showBinaryContent = true;

        Configuration conf = getConf();
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] fss = fs.listStatus(input);
        for (FileStatus status : fss) {
            Path path = status.getPath();
            // skips the _log or _SUCCESS files
            if (!path.getName().startsWith("part-")
                    && !path.getName().equals(input.getName()))
                continue;
            SequenceFile.Reader reader = new SequenceFile.Reader(fs, path, conf);
            Text key = new Text();
            BehemothDocument value = new BehemothDocument();
            while (reader.next(key, value)) {
                System.out.println(value.toString(showBinaryContent));
            }
            reader.close();
        }

        return 0;
    }
}
