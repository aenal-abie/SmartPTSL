package smartgis.project.app.smartgis.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import smartgis.project.app.smartgis.forms.GatherableFormFragment

class ViewPagerFormFragmentAdapter(
  fm: FragmentManager,
  private val forms: List<FormFragmentHolder>
) : FragmentPagerAdapter(fm) {
  override fun getItem(p0: Int): Fragment = forms[p0].fragment
  override fun getCount(): Int = forms.size
  override fun getPageTitle(position: Int): CharSequence? = forms[position].title
}

data class FormFragmentHolder(val title: String, val fragment: GatherableFormFragment)