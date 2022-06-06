
const Time = (props) => {

    let alarmTime= new Date (props.alarmDate);
    const month = alarmTime.toLocaleString("en-US", { month: "long" });
    const day = alarmTime.toLocaleString("en-US", { day: "2-digit" });
    const year = alarmTime.getFullYear();

    const time = alarmTime.getHours() + "h " + alarmTime.getMinutes() + "min " + alarmTime.getSeconds() + "s";

    return (
        <div>

            <div>
              {day}
            </div>

            <div>
                {month}
            </div>

            <div>
                {year}
            </div>

             <div>
                {time}
            </div>

        </div>


    )




}






export default Time;