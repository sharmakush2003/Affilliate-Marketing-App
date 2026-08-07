import sys

file_path = "app/src/main/java/com/rewardclub/app/MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    lines = f.readlines()

new_lines = []
skip = False
for i, line in enumerate(lines):
    # Remove showSplash logic
    if "var showSplash by remember { mutableStateOf(true) }" in line:
        continue
    if "if (showSplash) {" in line:
        continue
    if "SplashScreen(onTimeout = { showSplash = false })" in line:
        continue
    if "} else {" in line and "Surface(" in lines[i+1]:
        continue
    if "}" in line and i > 0 and "} else {" in lines[i-7] and "Surface(" in lines[i-6]:
        # we need to skip the closing brace of the else block. The } at line 94.
        # it is 7 lines after } else {
        continue

    # Remove SplashScreen composable
    if "@Composable" in line and "fun SplashScreen" in lines[i+1]:
        skip = True
    
    if skip:
        if "@Composable" in line and "fun DrawerContent" in lines[i+1]:
            skip = False
        else:
            continue

    # Replace NavyDark with TextDark in bottom nav bar
    if "selectedIconColor = NavyDark," in line:
        line = line.replace("NavyDark", "TextDark")
    if "selectedTextColor = NavyDark," in line:
        line = line.replace("NavyDark", "TextDark")
        
    new_lines.append(line)

with open(file_path, "w", encoding="utf-8") as f:
    f.writelines(new_lines)

print("MainActivity.kt updated successfully")
