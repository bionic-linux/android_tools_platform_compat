
import argparse
import re
import xml.etree.ElementTree as ET

parser = argparse.ArgumentParser(
    description="Generate docs for compat changes.")
parser.add_argument("--xml", type=argparse.FileType('r'), action='append',
                    help="config_config.xml files to read from.")

OutputTemplate = """
<html>
  <title>Compatibility changes</title>
  <body>
  <h1>Compatibilility changes</h1>
  {body}
  </body>
</html>
"""

ChangeTemplate = """
  <h2>{name}</h2>
  <dl>
    <dt>Change ID</dt>
    <dd>{id}</dd>
    <dt>Description</dt>
    <dd>{description}</dd>
  </dl>
"""


def readChanges(files):
    changes = []
    for xmlfile in files:
        tree = ET.parse(xmlfile)
        for change in tree.findall('compat-change'):
            changes.append(change.attrib)
    return changes


def formatJavadocTags(javadoc):
    p = re.compile(r'\{\@(?:code|link) (?P<content>.*)\}')
    return p.sub(r'<code>\g<content></code>', javadoc)


def writeHtml(changes):
    return OutputTemplate.format(body=''.join([
        ChangeTemplate.format(
            name= change['name'],
            id= change['id'],
            description= formatJavadocTags(change['description']),
        ) for change in changes
    ]))


args = parser.parse_args()
changes = readChanges(args.xml)
print(writeHtml(changes))
