import React from 'react'
import { Card, Grid, Box } from "@mui/material";
import DurationSelector from "../components/DurationSelector";
import { FormControl, Select, MenuItem } from "@mui/material";

const azureSubscriptionTypes = [
    {
        id: 1,
        name: "Azure Subscription 1",
        value: "azure",
    },
    {
        id: 2,
        name: "Azure Subscription 2",
        value: "azure-subscription-two",
    },
    {
        id: 3,
        name: "Azure Subscription 3",
        value: "azure-subscription-three",
    },
    {
        id: 4,
        name: "Azure Subscription 4",
        value: "azure-subscription-four",
    }
]

const BillingInformationCard = (props) => {
    const { handleMonthChange, months, setDateRange, setCalling, calling, azureSubscriptions = false, handleSubscriptionChange, azureSubscriptionValue = 'azure' } = props;
    // console.log("props", props)
    const newPropsCss = {
        backgroundColor: "#FFFF",
        // width: "340px",
        width: "90%",
        textAlign: "center",
        ":hover": {
            backgroundColor: "#FFFF",
            color: "black",
        },
        "&.Mui-selected": {
            backgroundColor: "#FFFF !important",
            color: "black",
        },
    };
    return (
        <>
            <Card sx={{ px: 2, py: 4, m: 2 }}>
                <Box
                    component={"div"}
                    sx={{
                        display: "flex",
                        justifyContent: "space-around",
                        alignItems: "center",
                    }}
                >
                    <Grid
                        container
                        // spacing={3}
                        spacing={{ xs: 1, md: 2, lg: 3 }}
                        // justifyContent="center"
                        alignItems="center"
                    >
                        <Grid item xs={12} lg={azureSubscriptions ? 12 : 4} xl={azureSubscriptions ? 12 : 4}>
                            <div className={`h4 fw-bold ${azureSubscriptions ? '' : ''}`}>Billing Information</div>
                        </Grid>
                        {azureSubscriptions ? <Grid item xs={12} sm={12} md={4} lg={4} xl={4} className='d-flex justify-content-center'>
                            <div style={{ width: '100%' }}>
                                <p className="p-0 m-0">Subscriptions</p>
                                <FormControl sx={{ ...newPropsCss }} fullWidth>
                                    <Select
                                        fullWidth
                                        labelId="demo-simple-select-label"
                                        id="demo-simple-select"
                                        className='demo-simple-select'
                                        sx={{ ...newPropsCss, height: "2.4em" }}
                                        value={azureSubscriptionValue}
                                        onChange={handleSubscriptionChange}
                                        //   onFocus={handleFocus}
                                        displayEmpty
                                        inputProps={{ 'aria-label': 'Without label' }}
                                        MenuProps={{ PaperProps: { sx: { maxHeight: 200 } } }}
                                    >
                                        {/* <MenuItem value="" disabled>
                                            Select Service
                                        </MenuItem> */}
                                        {azureSubscriptionTypes?.map((option, index) => (
                                            <MenuItem key={index} value={option.value} sx={{ ...newPropsCss }}>
                                                {option.name}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>

                            </div>
                        </Grid> : null}

                        <Grid item xs={12} sm={12} md={azureSubscriptions ? 4 : 6} lg={4} xl={4} className='d-flex justify-content-center'>

                            {props.children}
                        </Grid>

                        <Grid item xs={12} sm={12} md={azureSubscriptions ? 4 : 6} lg={4} xl={4} className='d-flex justify-content-center'>
                            <div style={{ width: '100%' }}>
                                <p className="p-0 m-0">Duration</p>
                                <DurationSelector
                                    handleMonthChange={handleMonthChange}
                                    months={months}
                                    setDateRange={setDateRange}
                                    setCalling={setCalling}
                                    calling={calling}
                                />
                            </div>
                        </Grid>
                    </Grid>
                </Box>
            </Card>
        </>
    )
}

export default BillingInformationCard