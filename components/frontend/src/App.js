import { useEffect, useReducer } from "react";
import { Layout } from "antd";
import { LogoutOutlined } from "@ant-design/icons";
import axios from "axios";
import { TableContent } from "./TableContent";
import { FormContent } from "./FormContent";
import './App.css';
import { WebSocket } from "./WebSocket";
import { AuthenticationRetainer } from "./AuthenticationRetainer"
import { canEdit } from "./rest/crud";
import { ModalAlarms } from "./ModalAlarms";

const initialState = {
  viewState: {},
  detail: false,
  edit: false,
  id: null,
  configResp: null,
  configReq: null,
  alarms: new Map(),
  editEnable: false
}, reducer = (state, action) => {
  let alarms;
  switch (action.type) {
    case "table":
      return { ...state, viewState: action.payload, detail: false };
    case "detail":
      return { ...state, ...action.payload };
    case "config-resp":
      return { ...state, configResp: action.payload };
    case "config-req":
      return { ...state, configReq: action.payload };
    case "alarm":
      alarms = new Map(state.alarms);
      alarms.set(action.payload.id, action.payload);
      return { ...state, alarms }
    case "toggle-alarm":
      alarms = new Map(state.alarms);
      alarms.delete(action.payload);
      return { ...state, alarms }
    case "userEnable":
      return { ...state, editEnable: action.payload };
    default:
      return state;
  }
};

function App() {
  const [{ viewState, detail, edit, id, configResp, configReq, alarms, editEnable }, dispatch] = useReducer(reducer, initialState);

  const { Header, Content, Footer } = Layout;

  useEffect(() => { canEdit().then(payload => dispatch({ type: "userEnable", payload })) }, [dispatch]);

  return (
    <Layout>
      <Header className="header">
        My IOT Device Handler
        <LogoutOutlined
          className="cp-logout"
          style={{ fontSize: 30, lineHeight: "64px" }}
          title={"Logout"}
          onClick={() => axios.post("/gateway/auth/logout").finally(() => window.location.reload())}
        />
      </Header>
      <Content className="content">
        {detail
          ?
          <FormContent {...{ edit, id, configResp, dispatch }} />
          :
          <TableContent {...{ viewState, editEnable, dispatch }} />
        }
      </Content>
      <Footer className="footer">
        <ModalAlarms alarmList={[...alarms.entries()]} {...{ dispatch }} />
      </Footer>
      <WebSocket {...{ dispatch }} request={configReq} />
      <AuthenticationRetainer />
    </Layout>
  );
}

export default App;