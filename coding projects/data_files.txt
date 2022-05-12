"""
Python script to format BackBlaze hard drive failure data for my final project in STAT 400.


Backblaze releases their data on a quarterly basis. In each quarter's folder of data, there
is a file for each day. Each day's worth of data has data from about 165,000 hard drives
with 149 columns for each row. Due to computational and time limits, we were not able to to 
use all of the data. Since our project was focusing on predicting failures, we decided to
select all failures found in a day along with a random sample of 200 data points from each 
day. The data points from each day was saved to a dataframe with the data from previous 
days. Once all files had been processed, the dataframe was saved to my STAT 400 folder as
a csv.

Data from Backblaze can be found here: https://www.backblaze.com/b2/hard-drive-test-data.html
"""
import pandas as pd


def find_df_sizes():
    # find an approximation for how many datapoints are in each day's worth of data
    file_path = f"C:/Users/Gabbie/Downloads/STAT400_data/data_Q1_2021/2021-01-01.csv"
    df = pd.read_csv(file_path)
    print(df.shape)


def make_df(date, quarter):
    file_path = f"C:/Users/Gabbie/Downloads/STAT400_data/data_Q{quarter}_2021/{date}"
    # only read certain columns from the dataframe 
    df_1 = pd.read_csv(file_path, 
                       usecols=['date', 'serial_number', 'model', 'failure', 'smart_5_raw', 'smart_187_raw', 
                       'smart_188_raw', 'smart_197_raw', 'smart_198_raw'])
    # add all failures for this day
    failures = df_1.loc[(df_1['failure']) == 1] 
    print(f"{failures.shape[0]} failures found in {date}")
    # take a random sample of 200 datapoints to add to the final file
    random_sample = df_1.sample(n=200)
    full_df = pd.concat([failures, random_sample], ignore_index=True)
    return full_df


def run_quarter(months, days, quarter):
    """Combine one quarter's worth of data into one file.
    
    Parameters:
    -----------
        months: (list) months that should be included this run
        days: (list) days in each of the months
        quarter: (int) quarter month(s) are in
    """
    year = "2021"
    df = pd.DataFrame(columns=['date', 'serial_number', 'model', 'failure', 'smart_5_raw', 'smart_187_raw', 
    'smart_188_raw', 'smart_197_raw', 'smart_198_raw'])
    for i in range (1, len(months) + 1):
        month = f"-0{months[i-1]}" if months[i-1] < 10 else f"-{months[i-1]}"
        for j in range(1, days[i-1] + 1):
            day = f"-0{j}.csv" if j < 10 else f"-{j}.csv"
            full_date = year + month + day
            try:
                new_df = make_df(full_date, quarter)
                df = pd.concat([df, new_df], ignore_index=True)
            except Exception:
                print(f"Error on file {full_date}!!!!!! Make sure to add this file again")
    
    return df


def combine_full_year():
    """Call run_quarter to run each quarter's worth of data a combine into a year-long dataframe."""
    q1 = run_quarter(months=[1,2,4], days =[31, 28, 31], quarter = 1)
    q2 = run_quarter(months=[4,5,6], days=[30, 31, 30], quarter = 2)
    q3 = run_quarter(months =[7,8, 9], days=[31,31, 30], quarter=3)
    q4 = run_quarter(months=[10,11,12], days=[31,30,31], quarter = 4)
    df = pd.DataFrame(columns=['date', 'serial_number', 'model', 'failure', 'smart_5_raw', 'smart_187_raw',
     'smart_188_raw', 'smart_197_raw', 'smart_198_raw'])
    df = pd.concat([df, q1], ignore_index=True)
    df = pd.concat([df, q2], ignore_index=True)
    df = pd.concat([df, q3], ignore_index=True)
    df = pd.concat([df, q4], ignore_index=True)
    df.to_csv(r"C:\Users\Gabbie\OneDrive - University of St. Thomas\STAT 400\backblaze_data_2021.csv",
                index=False)
    print("successfully wrote dataframe to csv! ")    


if __name__=="__main__":
    find_df_sizes()
    combine_full_year()
