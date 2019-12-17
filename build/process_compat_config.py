#!/usr/bin/env python
#
# Copyright (C) 2019 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""
Extracts compat_config.xml from built jar files and merges them into a single
XML file.
"""

import argparse
import sys
import xml.etree.ElementTree as ET
from zipfile import ZipFile

def extract_compat_config(jarfile):
    """
    Reads all compat_config.xml files from a jarfile.

    Yields: open filehandles for each XML file found.
    """
    with ZipFile(jarfile, 'r') as jar:
        for info in jar.infolist():
            if info.filename.endswith("/compat_config.xml"):
                with jar.open(info.filename, 'r') as xml:
                    yield xml

class ConfigMerger(object):

    def __init__(self):
        self.tree = ET.ElementTree()
        self.tree._setroot(ET.Element("config"))

    def merge(self, xmlFile):
        xml = ET.parse(xmlFile)
        for child in xml.getroot():
            self.tree.getroot().append(child)

    def write(self, filename):
        self.tree.write(filename, encoding='utf-8', xml_declaration=True)


def main(argv):
    parser = argparse.ArgumentParser(
        description="Processes compat config XML files")
    parser.add_argument("--jar", type=argparse.FileType('r'), action='append',
        help="Specifies a jar file to extract compat_config.xml from.")
    parser.add_argument("--xml", type=argparse.FileType('r'), action='append',
        help="Specifies an xml file to read compat_config from.")

    args = parser.parse_args()

    c = ConfigMerger()
    if args.jar:
        for jar in args.jar:
            for xml in extract_compat_config(jar):
                c.merge(xml)
    if args.xml:
        for xml in args.xml:
            c.merge(xml)

    c.write("/dev/stdout")



if __name__ == "__main__":
    main(sys.argv)