#!/bin/bash
set -e
# Make sure that additional entries are not added to any of the hiddenapi files.
LOCAL_DIR="$( dirname ${BASH_SOURCE} )"

SHA=$1

for file in $(git show --name-only --pretty=format: $SHA | grep "hiddenapi/hiddenapi-.*txt"); do
    ENTRIES=$(grep -E "^\+[^+]" <(git diff ${SHA}~1 ${SHA} $file) | sed "s|^\+||" || echo)
    if [[ -n "${ENTRIES}" ]]; then
      echo -e "\e[1m\e[31m$file $SHA contains the following entries\e[0m"
      echo -e "\e[1m\e[31mfor packages that are handled using UnsupportedAppUsage. Please remove\e[0m"
      echo -e "\e[1m\e[31mthese entries and add annotations instead.\e[0m"
      for i in ${ENTRIES}
      do
        echo -e "\e[33m  ${i}\e[0m"
      done
      EXIT_CODE=1
    fi
done
exit $EXIT_CODE
