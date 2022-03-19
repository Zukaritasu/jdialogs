// Copyright (C) 2021-2022 Zukaritasu
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

#include "jbase.hpp"

BOOL APIENTRY DllMain(HMODULE, DWORD reason, LPVOID)
{
	static HRESULT hr = E_FAIL;
	switch (reason) 
	{
		case DLL_PROCESS_DETACH:
			if (SUCCEEDED(hr))
				OleUninitialize();
			break;
		case DLL_PROCESS_ATTACH: 
		{
			// The Ole library is initialized to use the CoCreateInstance function. 
			if (FAILED(hr = OleInitialize(nullptr))) 
			{
				ShowError(nullptr, hr);
				return FALSE;
			}
			break;
		}
	}
	return TRUE;
}