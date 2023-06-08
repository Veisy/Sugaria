import re

import pandas as pd
import os

# Define the input Excel file path and the output directory for Android string files
input_file_path = 'output.xls'
output_dir = 'app/src/main/res/'
sheet_name = "Strings"


# TODO For projects other than TALISMANMOBILE, refactor this function.
def set_output_file_path(language):
    global output_dir
    if language == "en":
        file_name = 'values'
    else:
        file_name = 'values-' + language
    return os.path.join(output_dir, file_name, 'strings.xml')


def create_string_resource_element(resource_key, resource_value, data_type, is_translatable):
    is_translatable = is_translatable == 'true'
    content = "/>" if resource_value == "" else ">" + resource_value + "</string>"
    if data_type == 'string':
        translatable_attr = '' if is_translatable else ' translatable="false"'
        string_resource = f'    <string name="{resource_key}"{translatable_attr}{content}\n'
    elif data_type == 'string-array':
        translatable_attr = '' if is_translatable else ' translatable="false"'
        array_elements = re.findall(r'\[(.*?)]', resource_value)
        items = '\n'.join([f'        <item>{e}</item>' for e in array_elements])
        string_resource = f'    <string-array name="{resource_key}"{translatable_attr}>\n{items}\n    </string-array>\n'
    else:
        string_resource = f'    <string name="{resource_key}"{content}\n'

    return string_resource


# Load the Excel file into a Pandas DataFrame
df = pd.read_excel(input_file_path, sheet_name=sheet_name, keep_default_na=False)

# Get the unique keys in the DataFrame
keys = df['android_key'].unique()

languages = df.columns[4:]
languages = languages.insert(0, "en")

for lang in languages:
    # Create the output directory path for the current language
    output_file_path = set_output_file_path(lang)

    if lang == 'en':
        extra = 'tools:ignore="MissingTranslation, Typos"'
    else:
        extra = 'tools:ignore="ExtraTranslation"'
    with open(output_file_path, 'w', encoding="utf-8") as f:
        # Write the header to the XML file
        f.write(
            f'<?xml version="1.0" encoding="utf-8"?>\n<resources xmlns:tools="http://schemas.android.com/tools" {extra}>\n')

        # Loop through the keys and add the string values to the XML file
        for key in keys:
            # Filter the DataFrame to only include rows for the current key
            key_df = df[df['android_key'] == key]

            # Get the value for the current language and key
            value = key_df[lang].values[0]

            # Write the value to the output file
            if isinstance(value, str) and (lang == 'en' or value != ""):
                f.write(create_string_resource_element(resource_key=key, resource_value=value,
                                                       data_type=key_df['data_type'].values[0],
                                                       is_translatable=str(
                                                           key_df['is_translatable'].values[0]).lower()))

        # Write the footer to the XML file
        f.write('</resources>\n')
        print("Completed writing to " + output_file_path)
