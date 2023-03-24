library(tidyverse)

cleanText <- function(x) {
  return(
    x %>%
      str_replace_all("\t", " ") %>%
      str_replace_all("\\}", " \\} ") %>%
      str_replace_all("\\{", " \\{ ") %>%
      str_replace_all("=", " = ") %>%
      str_squish()
  )
}

file2Text <- function(file) {
  con <- file(file, open = "r")
  text <- readLines(con) %>%
    paste(collapse = '')
  close(con)
  return(text)
}

readDir <- "./statefiles"
writeDir <- "./statefiles_clean"

if (!dir.exists(writeDir)) {
  dir.create(writeDir)
  print("Created writing directory")
}

stateFileList <- list.files(readDir)
stateText <- stateFileList %>% 
  map(~paste0(readDir, "/", .)) %>% 
  map(file2Text) %>% 
  map(cleanText)
map2(stateText, map(stateFileList, ~paste0(writeDir, "/", .)), write)

patID <- "(?<=id\\s=\\s)\\d+"
stateID <- map(stateText, str_extract, patID)

patProvince <- "(?<=provinces\\s=\\s)\\{\\s*([^}]*)"
stateProvinces <- stateText %>%
  map(str_extract, pattern = patProvince, group = 1) %>%
  map(trimws) %>%
  map(str_split_1, pattern = " ")

patName <- "(?<=name\\s=\\s)([^\\s\\r]*)"
stateNames <- stateText %>%
  map(str_extract, pattern = patName, group = 1) %>%
  map(str_sub, start = 2L, end = -2L)

patStateCat <- "(?<=state_category\\s=\\s)([^\\s\\r]*)"
stateCats <- stateText %>%
  map(str_extract, pattern = patStateCat, group = 1)

patManpower <- "(?<=manpower\\s=\\s)\\d+"
stateManpower <- map(stateText, str_extract, patManpower)

mandatoryData <- tibble(stateID, stateNames, stateCats, stateManpower, stateProvinces) %>%
  unnest(c(stateID, stateNames, stateCats, stateManpower)) %>%
  unnest_longer(stateProvinces)

write_csv(mandatoryData, "./input/mandatoryData.csv")
