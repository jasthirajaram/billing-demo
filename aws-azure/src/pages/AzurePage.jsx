import React, { useEffect, useState } from "react";
import { Card, Grid } from "@mui/material";
import { azureService } from "../services/Services";
import DurationSelector from "../components/DurationSelector";
import Sidenav from "../components/Sidenav";
import Navbar from "../components/Navbar";
import { Box } from "@mui/material";
import Typography from "@mui/material/Typography";
import AzureSelector from "../components/Azure/AzureSelector";
import AzureTable from "../tables/AzureTable";
import CustomBarChart from "../components/CustomBarChart";
import CustomPieChart from "../components/CustomPieChart";

export const AzurePage = () => {
  const [ResourseType, setResourseType] = useState("");
  const [sidenavOpen, setSidenavOpen] = useState(false);
  const [dateRange, setDateRange] = useState({
    startDate: null,
    endDate: null,
  });
  const [months, setMonths] = useState(1);
  const [display, setDisplay] = useState(false);
  const [data, setData] = useState([]);
  const [calling, setCalling] = useState(true);
  console.log("dateRange", dateRange);
  useEffect(() => {
    forAzureGet();
  }, [calling]);

  const handleMonthChange = (selectedMonth) => {
    console.log("selectedMjjhhdshfkhonth", selectedMonth);
    setMonths(selectedMonth);
    setDisplay(true);
    setCalling(!calling);
  };

  const handleServiceChange = (event) => {
    setResourseType(event.target.value);
    setCalling(!calling);
  };

  const toggleSidenav = () => {
    setSidenavOpen(!sidenavOpen);
  };

  const forAzureGet = async () => {
    azureService(ResourseType, dateRange?.startDate, dateRange?.endDate, months)
      .then((res) => {
        console.log(res);
        setData(res);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const bodyStyle = {
    backgroundColor: "#f0f0f0",
    minHeight: "100vh",
    padding: "20px",
    overflowX: "hidden",
  };

  const contentStyle = {
    transition: "margin-left 0.5s",
    marginLeft: sidenavOpen ? 250 : 0,
    width: "100%",
    // flexGrow: 1,
  };

  useEffect(() => {
    setDisplay(true);
  }, [display]);

  useEffect(() => {
    const savedService = localStorage.getItem("service");
    if (savedService) setResourseType(savedService);
  }, []);

  const monthData = Array.isArray(data?.monthlyTotalAmounts)
    ? data.monthlyTotalAmounts.map((item) => ({
        name: Object.keys(item)[0],
        amount: Object.values(item)[0]?.toFixed(2),
      }))
    : [];

  const topFiveCustomers = data?.top5Services?.map((item) => {
    const { resourceType, totalCost } = item;
    return {
      name: resourceType,
      value: totalCost,
    };
  });

  return (
    <div style={bodyStyle}>
      <React.Fragment>
        <Navbar toggleSidenav={toggleSidenav} />
        <Box height={50} />
        <Box sx={{ display: "flex" }}>
          <Sidenav open={sidenavOpen} onClose={toggleSidenav} />
          <Box component="main" sx={{ ...contentStyle }}>
            <Typography
              variant="h5"
              sx={{ marginBottom: 3, textAlign: "center" }}
            >
              Azure Billing-Details
            </Typography>
            <Card sx={{ px: 2, py: 4, m: 2 }}>
              <Box
                component="div"
                sx={{
                  display: "flex",
                  justifyContent: "space-around",
                  alignItems: "center",
                }}
              >
                <Grid container spacing={3} alignItems="center">
                  <Grid item xs={12} sm={6} md={6} lg={4} xl={4}>
                    <div className="h3 fw-bold">Billing Information</div>
                  </Grid>
                  <Grid item xs={12} sm={6} md={6} lg={4} xl={4}>
                    <div>
                      <h5>Service</h5>
                      <AzureSelector
                        ResourseType={ResourseType}
                        handleServiceChange={handleServiceChange}
                      />
                    </div>
                  </Grid>
                  <Grid item xs={12} sm={6} md={6} lg={2} xl={2}>
                    <div>
                      <h5>Duration</h5>
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

            <Grid
              container
              spacing={3}
              style={{ marginLeft: "-10px", width: "100%" }}
            >
              {/* Barchart */}
              <Grid item xs={11.2} md={6} lg={8}>
                <div className="card p-3">
                  <div className="fw-bold h5">Billing Summary</div>
                  <CustomBarChart
                    data={data && monthData}
                    height={460}
                    barLineSize={60}
                    colors={["#10B981", "#FE6476", "#FEA37C", "#048DAD"]}
                  />
                </div>
              </Grid>

              {/* Total amount */}
              <Grid
                item
                xs={11.2}
                md={6}
                lg={4}
                style={{ padding: "24px 5px 0 20px" }}
              >
                <div className="card p-3">
                  <div className="p-3">
                    <span className="h5 fw-bold">Billing Period </span>
                    <span className="h5 fw-bold">
                      ({data?.billingPeriod?.map((i) => i?.BillingPeriod)})
                    </span>
                  </div>
                  <div className="d-flex justify-content-center">
                    <span style={{ fontSize: "20px" }}>Total Amount-</span>
                    <span
                      style={{
                        fontSize: "20px",
                        color: "#10B981",
                        paddingLeft: "4px",
                      }}
                    >
                      <span className="px-1 fw-bold">
                        {"₹"} {data?.totalAmount && data?.totalAmount?.toFixed(2)}
                      </span>
                    </span>
                  </div>
                </div>
                <div className="card p-3 mt-2">
                  <div className="p-3">
                    <div className="h5 fw-bold">Top 5 Consumers</div>
                    <CustomPieChart
                      data={data?.top5Services && topFiveCustomers}
                      height={300}
                    />
                  </div>
                </div>
              </Grid>
            </Grid>

            <Card sx={{ px: 2, py: 4, m: 2 }}>
              <Box
                component="div"
                sx={{
                  display: "flex",
                  justifyContent: "space-around",
                  alignItems: "center",
                }}
              >
                <Grid container spacing={2} className="mb-3">
                  <Grid
                    item
                    xs={11}
                    sm={11}
                    lg={12}
                    className="mx-auto mx-sm-0"
                  >
                    <AzureTable data={data && data?.billingDetails} />
                  </Grid>
                </Grid>
              </Box>
            </Card>
          </Box>
        </Box>
      </React.Fragment>
    </div>
  );
};

export default AzurePage;
