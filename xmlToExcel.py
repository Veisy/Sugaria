import os
import xlwt
import re
from lxml import etree

# Define path to the Android project and languages
android_project_path = "app/src/main/res/"
# Define path to the output Excel file
output_file_path = "output.xls"
sheet_name = "Strings"


def extract_string_text(string_tree):
    # We use regular expressions on raw etree string to extract text value instead of string_tree.text
    # because any angle brackets inside string element is read as a child tree, for example html tags.
    # There are some other problems with string.text, for example it doesn't read CDATA tags.
    tree_as_raw_string = etree.tostring(string_tree, encoding='utf-8', method='xml').decode('utf-8')
    pattern = r'<string.*?>(.*?)<\/string>|<string.*?\/>'
    match = re.search(pattern, tree_as_raw_string, re.DOTALL)
    if match:
        string_tree_text = match.group(1)
        if string_tree_text is None:
            string_tree_text = ""
            print(string_tree_text)
    else:
        string_tree_text = string_tree.text
        print(string_tree_text)
    return string_tree_text


def get_languages(values_dir):
    # Get the list of language codes from the "values" folders in the Android project
    languages = []
    # Languages maybe in the form of 'values-tr' or 'values-tr-rTR'
    regex_languages = re.compile(r'^values-([a-z]{2})(-r[A-Z]{2})?$')
    languages.append("en")
    for filename in os.listdir(values_dir):
        match = regex_languages.match(filename)
        if match:
            language = filename.split('-', 1)[1]
            if language not in languages:
                languages.append(language)

    # make turkish as second language
    if "tr" in languages:
        languages.insert(1, languages.pop(languages.index('tr')))
    elif "tr-rTR" in languages:
        languages.insert(1, languages.pop(languages.index('tr-rTR')))
    return languages


def get_string_array_elements_as_string(_string_array):
    items = [elem.text for elem in _string_array.findall('item')]
    items_bracketed_list = ['[{}]'.format(element) for element in items]
    return '  '.join(items_bracketed_list)


# Define styles
missing_lang_style = xlwt.easyxf('pattern: pattern solid, fore_colour coral;')
non_translatable_style = xlwt.easyxf('align: wrap on, vert top; pattern: pattern solid, fore_colour light_orange;')
normal_header_style = xlwt.easyxf('font: bold on, height 300; align: horiz center; borders: top double, bottom double;')
language_header_style = xlwt.easyxf('font: bold on, height 300; align: horiz center; '
                                    'borders: top double, bottom double; pattern: '
                                    'pattern solid, fore_colour light_green;')
text_style = xlwt.easyxf('align: wrap on, vert top')

# Create workbook and worksheet
workbook = xlwt.Workbook()
worksheet = workbook.add_sheet(sheet_name)
# Write header row and freeze it
worksheet.write(0, 0, "android_key", normal_header_style)
worksheet.write(0, 1, "data_type", normal_header_style)
worksheet.write(0, 2, "is_translatable", normal_header_style)
worksheet.set_panes_frozen(True)
worksheet.set_horz_split_pos(1)

language_list = get_languages(os.path.join(android_project_path))
# Parse XML files for each language and extract string resources
english_strings = {}
lang_strings = {}
for i, lang in enumerate(language_list):
    if lang == "en":
        worksheet.write(0, 3, lang, language_header_style)
    else:
        worksheet.write(0, i + 3, lang[:2], language_header_style)
        lang_strings[lang] = {}

    lang_dir = os.path.join(android_project_path, "values" if lang == "en" else "values-" + lang)
    print(lang_dir)

    xml_path = os.path.join(lang_dir, "strings.xml")
    parser = etree.XMLParser(encoding="utf-8", strip_cdata=False, resolve_entities=False)
    tree = etree.parse(xml_path, parser=parser)
    root = tree.getroot()

    # Get strings from XML
    for string in root.findall('string'):
        string_text = extract_string_text(string)
        if lang == "en":
            english_strings[string.get('name')] = {"text": string_text,
                                                   "data_type": "string",
                                                   "translatable": string.get('translatable')}
        else:
            lang_strings[lang][string.get('name')] = string_text
    # Get string arrays from XML
    for string_array in root.findall('string-array'):
        if lang == "en":
            english_strings[string_array.get('name')] = {"text": get_string_array_elements_as_string(string_array),
                                                         "data_type": "string-array",
                                                         "translatable": string_array.get('translatable')}
        else:
            lang_strings[lang][string_array.get('name')] = get_string_array_elements_as_string(string_array)

# Write data to worksheet
unTranslatableRows = []
for i, key in enumerate(english_strings.keys(), start=1):
    # Determine if the string is translatable or not and set the appropriate style
    if english_strings[key]["translatable"] == "false":
        is_translatable = str('false')
        style = non_translatable_style
        unTranslatableRows.append(i)
    else:
        is_translatable = str('true')
        style = text_style

    worksheet.write(i, 0, key, style)
    worksheet.write(i, 1, english_strings[key]["data_type"], style)
    worksheet.write(i, 2, is_translatable, style)
    worksheet.write(i, 3, english_strings[key]["text"], style)

    # Write the translated strings to the worksheet
    for j, lang in enumerate(language_list[1:], start=4):
        if key in lang_strings[lang]:
            worksheet.write(i, j, lang_strings[lang][key], text_style)
        else:
            if i in unTranslatableRows:
                style = non_translatable_style
            else:
                style = missing_lang_style
            worksheet.write(i, j, "", style)

# Set the weight of each row
worksheet.col(0).width = 40 * 256
for i in range(1, len(language_list) + 3):
    worksheet.col(i).width = 30 * 256

# Set the height of each row
for i in range(1, len(english_strings) + 3):
    worksheet.row(i).height = 2 * 256

workbook.save(output_file_path)

print("Excel file created successfully!")
